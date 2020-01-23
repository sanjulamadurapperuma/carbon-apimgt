package org.wso2.carbon.apimgt.gateway.mediators.oauth;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.apimgt.gateway.mediators.oauth.client.OAuthClient;
import org.wso2.carbon.apimgt.gateway.mediators.oauth.client.TokenResponse;
import org.wso2.carbon.apimgt.gateway.mediators.oauth.conf.OAuthEndpoint;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * OAuth Token generator scheduled executor
 */
public class TokenGeneratorScheduledExecutor {
    private static final Log log = LogFactory.getLog(TokenGeneratorScheduledExecutor.class);

    private ScheduledExecutorService executorService;

    public TokenGeneratorScheduledExecutor() {
        this.executorService = new ScheduledThreadPoolExecutor(5);
    }

    /**
     * Initialize OAuth Scheduled Executor
     */
    public void schedule(OAuthEndpoint oAuthEndpoint) {
        try {
            log.info("Scheduling token generator for token endpoint " + getEndpointId(oAuthEndpoint));

            executorService.scheduleAtFixedRate(()->{
                try {
                    log.info("Generating access token: " + getEndpointId(oAuthEndpoint));

                    TokenResponse tokenResponse = OAuthClient.generateToken(oAuthEndpoint.getTokenApiUrl(),
                            oAuthEndpoint.getApiKey(), oAuthEndpoint.getApiSecret(), oAuthEndpoint.getGrantType());
                    assert tokenResponse != null;
                    log.info("Access token generated: " + getEndpointId(oAuthEndpoint)
                            + " [access-token] " + tokenResponse.getAccessToken());
                    TokenCache.getInstance().getTokenMap().put(oAuthEndpoint.getId(), tokenResponse.getAccessToken());
                } catch (Exception e) {
                    log.error("Could not generate access token " + getEndpointId(oAuthEndpoint), e);
                }
            }, 0, oAuthEndpoint.getTokenRefreshInterval(), TimeUnit.SECONDS);
        } catch (Exception e) {
            // Replace the log with a meaningful message or APIManagementException
            log.error(e);
        }
    }

    private String getEndpointId(OAuthEndpoint oAuthEndpoint) {
        return "[id] " + oAuthEndpoint.getId() + " [url] " + oAuthEndpoint.getTokenApiUrl();
    }
}
