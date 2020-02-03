/*
 * Copyright (c) 2019, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.wso2.carbon.apimgt.gateway.mediators.oauth;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.synapse.ManagedLifecycle;
import org.apache.synapse.MessageContext;
import org.apache.synapse.core.SynapseEnvironment;
import org.apache.synapse.core.axis2.Axis2MessageContext;
import org.apache.synapse.mediators.AbstractMediator;
import org.json.simple.JSONObject;
import org.wso2.carbon.apimgt.gateway.mediators.oauth.client.TokenResponse;
import org.wso2.carbon.apimgt.gateway.mediators.oauth.conf.OAuthEndpoint;
import org.wso2.carbon.apimgt.impl.APIConstants;
import org.wso2.carbon.apimgt.impl.APIConstants.OAuthConstants;
import org.wso2.carbon.apimgt.impl.APIManagerConfiguration;
import org.wso2.carbon.apimgt.impl.internal.ServiceReferenceHolder;
import org.wso2.carbon.core.util.CryptoException;
import org.wso2.carbon.core.util.CryptoUtil;
import java.util.UUID;

import java.util.Map;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;

/**
 * OAuth Mediator for generating OAuth tokens for invoking service endpoints secured with OAuth
 */
public class OAuthMediator extends AbstractMediator implements ManagedLifecycle {

    private static final Log log = LogFactory.getLog(OAuthMediator.class);
    public static final ScheduledExecutorService executorService;


    static {
        log.info("Initializing OAuth Mediator...");

         executorService = new ScheduledThreadPoolExecutor(5);
         log.info("ScheduledThreadPoolExecutor object instantiated...");
    }

    // Interface methods are being implemented here
    @Override
    public void init(SynapseEnvironment synapseEnvironment) {
        // Ignore
    }

    @Override
    public void destroy() {
        //Ignore
    }

    @Override
    public boolean mediate(MessageContext messageContext) {
        if(log.isDebugEnabled()) {
            log.debug("OAuth Mediator is invoked...");
        }

        CryptoUtil cryptoUtil = CryptoUtil.getDefaultCryptoUtil();
        try {
            String tokenApiUrl = (String) messageContext.getProperty(OAuthConstants.TOKEN_API_URL);
            String apiKey = (String) messageContext.getProperty(OAuthConstants.OAUTH_API_KEY);
            String apiSecret = (String) messageContext.getProperty(OAuthConstants.OAUTH_API_SECRET);
            String grantType = (String) messageContext.getProperty(OAuthConstants.GRANT_TYPE);

            String decryptedApiKey = new String(cryptoUtil.base64DecodeAndDecrypt(apiKey));
            String decryptedApiSecret = new String(cryptoUtil.base64DecodeAndDecrypt(apiSecret));

            JSONObject oAuthEndpointSecurityProperties = getOAuthEndpointSecurityProperties();
            int tokenRefreshInterval;
            if (oAuthEndpointSecurityProperties != null) {
                tokenRefreshInterval = Integer.parseInt((String) oAuthEndpointSecurityProperties.
                        get(OAuthConstants.TOKEN_REFRESH_INTERVAL));
            } else {
                log.info("The Token Refresh Interval has not been set correctly in the config...");
                tokenRefreshInterval = 20;
            }

            OAuthEndpoint oAuthEndpoint = new OAuthEndpoint();
            oAuthEndpoint.setId((UUID.randomUUID().toString()));
            oAuthEndpoint.setTokenApiUrl(tokenApiUrl);
            oAuthEndpoint.setApiKey(decryptedApiKey);
            oAuthEndpoint.setApiSecret(decryptedApiSecret);
            oAuthEndpoint.setGrantType(grantType);
            oAuthEndpoint.setTokenRefreshInterval(tokenRefreshInterval);

            if (oAuthEndpoint != null) {
                try {
                    log.info("Generating access token...");

                    TokenGeneratorScheduledExecutor scheduledExecutor = new TokenGeneratorScheduledExecutor(executorService);
                    scheduledExecutor.schedule(oAuthEndpoint);
                } catch(Exception e) {
                    log.error("Could not generate access token...", e);
                }
            }

            TokenResponse tokenResponse = TokenCache.getInstance().getTokenMap().get(oAuthEndpoint.getId());
            if (tokenResponse != null) {
                String accessToken = tokenResponse.getAccessToken();
                Map<String, Object> transportHeaders = (Map<String, Object>) ((Axis2MessageContext) messageContext)
                        .getAxis2MessageContext().getProperty("TRANSPORT_HEADERS");
                transportHeaders.put("Authorization", "Bearer " + accessToken);
                log.debug("Access token set: " + accessToken);
            } else {
                log.debug("Token Response is null...");
            }
        } catch (CryptoException e) {
            log.error(" Error occurred when decrypting the client key and client secret", e);
        }
        return true;
    }

    /**
     * This method returns the OAuthEndpointSecurity Properties from the API Manager Configuration
     * @return JSONObject OAuthEndpointSecurity properties
     */
    public JSONObject getOAuthEndpointSecurityProperties() {
        APIManagerConfiguration configuration = ServiceReferenceHolder.getInstance()
                .getAPIManagerConfigurationService().getAPIManagerConfiguration();
        String tokenRefreshInterval = configuration.getFirstProperty(APIConstants
                .OAuthConstants.OAUTH_TOKEN_REFRESH_INTERVAL);

        JSONObject configProperties = new JSONObject();

        if (StringUtils.isNotEmpty(tokenRefreshInterval)) {
            configProperties.put(APIConstants.OAuthConstants.TOKEN_REFRESH_INTERVAL, tokenRefreshInterval);
            return configProperties;
        }
        return null;
    }
}
