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
import org.wso2.carbon.apimgt.api.APIManagementException;
import org.wso2.carbon.apimgt.gateway.mediators.oauth.client.OAuthClient;
import org.wso2.carbon.apimgt.gateway.mediators.oauth.client.TokenResponse;
import org.wso2.carbon.apimgt.gateway.mediators.oauth.conf.OAuthEndpoint;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * OAuth Token generator scheduled executor
 */
public class TokenGeneratorScheduledExecutor {
    private static final Log log = LogFactory.getLog(TokenGeneratorScheduledExecutor.class);

    private ScheduledExecutorService executorService;

    public TokenGeneratorScheduledExecutor(ScheduledExecutorService executorService) {
        this.executorService = executorService;
    }

    /**
     * Initialize OAuth Scheduled Executor
     */
    public void schedule(OAuthEndpoint oAuthEndpoint, CountDownLatch latch) {
        try {
            executorService.scheduleAtFixedRate(()->{
                try {
                    TokenResponse previousResponse = TokenCache.getInstance().getTokenMap().get(oAuthEndpoint.getId());
                    if (previousResponse != null) {
                        long validTill = previousResponse.getValidTill();
                        long currentTimeInSeconds = System.currentTimeMillis() / 1000;
                        long timeDifference = validTill - currentTimeInSeconds;

                        if (timeDifference <= 1) {
                            if (previousResponse.getRefreshToken() != null) {
                                generateToken(oAuthEndpoint, previousResponse.getRefreshToken());
                            } else {
                                generateToken(oAuthEndpoint, null);
                            }
                        }
                    } else {
                        generateToken(oAuthEndpoint, null);
                    }
                } catch (Exception e) {
                    log.error("Could not generate access token " + getEndpointId(oAuthEndpoint), e);
                }
                latch.countDown();
            }, 0, oAuthEndpoint.getTokenRefreshInterval(), TimeUnit.SECONDS);
        } catch (Exception e) {
            log.error("Error occurred when running the Token Generator " +  getEndpointId(oAuthEndpoint), e);
        }
    }

    public static void generateToken(OAuthEndpoint oAuthEndpoint, String refreshToken)
            throws IOException, APIManagementException {
        TokenResponse tokenResponse = OAuthClient.generateToken(oAuthEndpoint.getTokenApiUrl(),
                oAuthEndpoint.getClientId(), oAuthEndpoint.getClientSecret(), oAuthEndpoint.getUsername(),
                oAuthEndpoint.getPassword(), oAuthEndpoint.getGrantType(), oAuthEndpoint.getCustomParameters(),
                refreshToken);
        assert tokenResponse != null;
        TokenCache.getInstance().getTokenMap().put(oAuthEndpoint.getId(), tokenResponse);
    }

    private String getEndpointId(OAuthEndpoint oAuthEndpoint) {
        return "[id] " + oAuthEndpoint.getId() + " [url] " + oAuthEndpoint.getTokenApiUrl();
    }
}
