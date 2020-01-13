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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.synapse.ManagedLifecycle;
import org.apache.synapse.MessageContext;
import org.apache.synapse.core.SynapseEnvironment;
import org.apache.synapse.core.axis2.Axis2MessageContext;
import org.apache.synapse.mediators.AbstractMediator;
import org.wso2.carbon.apimgt.gateway.mediators.oauth.conf.OAuthEndpoint;
import org.wso2.carbon.apimgt.impl.APIConstants.OAuthConstants;

import java.util.Map;

/**
 * OAuth Mediator for generating OAuth tokens for invoking service endpoints secured with OAuth
 */
public class OAuthMediator extends AbstractMediator implements ManagedLifecycle {

    private static final Log log = LogFactory.getLog(OAuthMediator.class);

    private String endpointId;

    static {
        log.info("Initializing OAuth Mediator...");

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

        String tokenApiUrl = (String) messageContext.getProperty(OAuthConstants.TOKEN_API_URL);
        String apiKey = (String) messageContext.getProperty(OAuthConstants.OAUTH_API_KEY);
        String apiSecret = (String) messageContext.getProperty(OAuthConstants.OAUTH_API_SECRET);
        String grantType = (String) messageContext.getProperty(OAuthConstants.GRANT_TYPE);

        OAuthEndpoint oAuthEndpoint = new OAuthEndpoint();
        oAuthEndpoint.setTokenApiUrl(tokenApiUrl);
        oAuthEndpoint.setApiKey(apiKey);
        oAuthEndpoint.setApiSecret(apiSecret);
        oAuthEndpoint.setGrantType(grantType);

        TokenGeneratorScheduledExecutor scheduledExecutor = new TokenGeneratorScheduledExecutor();
        if (oAuthEndpoint != null) {
            scheduledExecutor.schedule(oAuthEndpoint);
        }

        String accessToken = TokenCache.getInstance().getTokenMap().get(getEndpointId());
        Map<String, Object> transportHeaders = (Map<String, Object>) ((Axis2MessageContext)messageContext)
                .getAxis2MessageContext().getProperty("TRANSPORT_HEADERS");
        transportHeaders.put("Authorization", "Bearer " + accessToken);
        log.debug("Access token set: " + accessToken);
        return true;
    }

    public String getEndpointId() {
        return endpointId;
    }

    public void setEndpointId(String endpointId) {
        this.endpointId = endpointId;
    }
}
