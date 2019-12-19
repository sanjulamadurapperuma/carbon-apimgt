package org.wso2.carbon.apimgt.gateway.mediators.oauth;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.apimgt.gateway.mediators.oauth.client.OAuthClient;
import org.wso2.carbon.apimgt.gateway.mediators.oauth.client.domain.TokenResponse;
import org.wso2.carbon.apimgt.gateway.mediators.oauth.conf.OAuthEndpoint;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * OAuth Token Generator Token Executor
 */
public class TokenGeneratorScheduledExecutor {
    private static final Log log = LogFactory.getLog(TokenGeneratorScheduledExecutor.class);

    private ScheduledExecutorService scheduledExecutorService;

    /**
     * Public constructor which creates a new ScheduledThreadPoolExecutor object
     */
    public TokenGeneratorScheduledExecutor() {
        this.scheduledExecutorService = new ScheduledThreadPoolExecutor(5);
    }

    /**
     * Initialize the oauth client scheduled executor
     */
    public void schedule(OAuthEndpoint oAuthEndpoint) {
        try {
            log.info("Scheduling token generator for token endpoint " + getEndpointId(oAuthEndpoint));

            scheduledExecutorService.scheduleAtFixedRate(()-> {
                try {
                    log.info("Generating access token: " + getEndpointId(oAuthEndpoint));

                    TokenResponse tokenResponse = OAuthClient.generateToken(oAuthEndpoint.getTokenApiUrl(),
                            oAuthEndpoint.getApiKey(), oAuthEndpoint.getApiSecret(), oAuthEndpoint.getGrantType());
                    log.info("Access Token generated: " + getEndpointId(oAuthEndpoint)
                    + " [access-token] " + tokenResponse.getAccessToken());
                    TokenCache.getInstance().getTokenMap().put(oAuthEndpoint.getId(), tokenResponse.getAccessToken());
                } catch(Exception e) {
                    log.error("Could not generated access token " + getEndpointId(oAuthEndpoint), e);
                }
            }, 0, oAuthEndpoint.getTokenRefreshInterval(), TimeUnit.SECONDS);
        } catch (Exception e) {
            log.error(e);
        }
    }

    /**
     * Return the OAuthEndpoint ID and Token URL
     * @param oAuthEndpoint object
     * @return String
     */
    private String getEndpointId(OAuthEndpoint oAuthEndpoint) {
        return "[id] " + oAuthEndpoint.getId() + " [url] " + oAuthEndpoint.getTokenApiUrl();
    }
}
