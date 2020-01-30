package org.wso2.carbon.apimgt.gateway.mediators.oauth;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.apimgt.api.APIManagementException;
import org.wso2.carbon.apimgt.gateway.mediators.oauth.client.OAuthClient;
import org.wso2.carbon.apimgt.gateway.mediators.oauth.client.TokenResponse;
import org.wso2.carbon.apimgt.gateway.mediators.oauth.conf.OAuthEndpoint;

import java.io.IOException;
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
    public void schedule(OAuthEndpoint oAuthEndpoint) {
        try {

            executorService.scheduleAtFixedRate(()->{ log.info("Running scheduler...");
                try {
                    TokenResponse previousResponse = TokenCache.getInstance().getTokenMap().get(oAuthEndpoint.getId());
                    if (previousResponse != null) {
                        long validTill = previousResponse.getValidTill();
                        long currentTimeInSeconds = System.currentTimeMillis() / 1000;
                        long timeDifference = validTill - currentTimeInSeconds;

                        if (timeDifference <= 0) {
                            generateToken(oAuthEndpoint);
                        }
                    } else {
                        generateToken(oAuthEndpoint);
                    }
                } catch (Exception e) {
                    log.error("Could not generate access token " + getEndpointId(oAuthEndpoint), e);
                }
            }, 0, oAuthEndpoint.getTokenRefreshInterval(), TimeUnit.SECONDS);
        } catch (Exception e) {
            log.error("Error occurred when running the Token Generator " +  getEndpointId(oAuthEndpoint), e);
        }
    }

    private void generateToken(OAuthEndpoint oAuthEndpoint) throws IOException, APIManagementException {
        TokenResponse tokenResponse = OAuthClient.generateToken(oAuthEndpoint.getTokenApiUrl(),
                oAuthEndpoint.getApiKey(), oAuthEndpoint.getApiSecret(), oAuthEndpoint.getGrantType());
        assert tokenResponse != null;
        TokenCache.getInstance().getTokenMap().put(oAuthEndpoint.getId(), tokenResponse);
    }

    private String getEndpointId(OAuthEndpoint oAuthEndpoint) {
        return "[id] " + oAuthEndpoint.getId() + " [url] " + oAuthEndpoint.getTokenApiUrl();
    }
}
