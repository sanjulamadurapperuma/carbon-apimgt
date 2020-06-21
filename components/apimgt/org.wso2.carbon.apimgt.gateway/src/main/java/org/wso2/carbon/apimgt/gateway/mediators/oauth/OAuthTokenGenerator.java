/*
 * Copyright (c) 2020, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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
import org.json.simple.JSONObject;
import org.wso2.carbon.apimgt.api.APIManagementException;
import org.wso2.carbon.apimgt.gateway.handlers.security.APISecurityConstants;
import org.wso2.carbon.apimgt.gateway.handlers.security.APISecurityException;
import org.wso2.carbon.apimgt.gateway.mediators.oauth.client.OAuthClient;
import org.wso2.carbon.apimgt.gateway.mediators.oauth.client.TokenResponse;
import org.wso2.carbon.apimgt.gateway.mediators.oauth.conf.OAuthEndpoint;
import org.wso2.carbon.apimgt.impl.APIConstants;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

public class OAuthTokenGenerator {
    private static final Log log = LogFactory.getLog(OAuthTokenGenerator.class);
    private boolean isRedisEnabled = false;
    private RedisCache redisCache = null;

    public void checkTokenValidity(OAuthEndpoint oAuthEndpoint, CountDownLatch latch, JSONObject oAuthEndpointSecurityProperties)
            throws APISecurityException {
        try {
            TokenResponse previousResponse = null;
            if (oAuthEndpointSecurityProperties != null) {
                // TODO
                isRedisEnabled = Boolean.parseBoolean((String) oAuthEndpointSecurityProperties
                        .get(APIConstants.OAuthConstants.IS_REDIS_ENABLED));
                String redisHost = (String) oAuthEndpointSecurityProperties.get(APIConstants.OAuthConstants.REDIS_HOST);
                String redisPort = (String) oAuthEndpointSecurityProperties.get(APIConstants.OAuthConstants.REDIS_PORT);
                if (oAuthEndpointSecurityProperties.containsKey(APIConstants.OAuthConstants.REDIS_PASSWORD)) {
                    String redisPassword = (String) oAuthEndpointSecurityProperties
                            .get(APIConstants.OAuthConstants.REDIS_PASSWORD);
                    redisCache = new RedisCache(redisHost, Integer.valueOf(redisPort), redisPassword);
                } else {
                    redisCache = new RedisCache(redisHost, Integer.valueOf(redisPort), null);
                }
                previousResponse = redisCache.getTokenResponseById(oAuthEndpoint.getId());
            } else {
                previousResponse = TokenCache.getInstance().getTokenMap().get(oAuthEndpoint.getId());
            }
            if (previousResponse != null) {
                long validTill = previousResponse.getValidTill();
                long currentTimeInSeconds = System.currentTimeMillis() / 1000;
                long timeDifference = validTill - currentTimeInSeconds;

                if (timeDifference <= 1) {
                    if (previousResponse.getRefreshToken() != null) {
                        addTokenToCache(oAuthEndpoint, previousResponse.getRefreshToken());
                    } else {
                        addTokenToCache(oAuthEndpoint, null);
                    }
                }
            } else {
                addTokenToCache(oAuthEndpoint, null);
            }
        } catch (IOException e) {
            log.error("Error while generating OAuth Token" + getEndpointId(oAuthEndpoint));
            throw new APISecurityException(APISecurityConstants.API_AUTH_INVALID_CREDENTIALS,
                    APISecurityConstants.API_AUTH_INVALID_CREDENTIALS_MESSAGE, e);
        } catch (APIManagementException e) {
            log.error("Could not retrieve OAuth Token" + getEndpointId(oAuthEndpoint));
            throw new APISecurityException(APISecurityConstants.API_AUTH_GENERAL_ERROR,
                    "Error while parsing OAuth Token response", e);
        }
        latch.countDown();
    }

    private void addTokenToCache(OAuthEndpoint oAuthEndpoint, String refreshToken)
            throws IOException, APIManagementException {
        TokenResponse tokenResponse = OAuthClient.generateToken(oAuthEndpoint.getTokenApiUrl(),
                oAuthEndpoint.getClientId(), oAuthEndpoint.getClientSecret(), oAuthEndpoint.getUsername(),
                oAuthEndpoint.getPassword(), oAuthEndpoint.getGrantType(), oAuthEndpoint.getCustomParameters(),
                refreshToken);
        assert tokenResponse != null;
        if (isRedisEnabled) {
            redisCache.addTokenResponse(oAuthEndpoint.getId(), tokenResponse);
        } else {
            TokenCache.getInstance().getTokenMap().put(oAuthEndpoint.getId(), tokenResponse);
        }
    }

    private String getEndpointId(OAuthEndpoint oAuthEndpoint) {
        return "[url] " + oAuthEndpoint.getTokenApiUrl();
    }
}
