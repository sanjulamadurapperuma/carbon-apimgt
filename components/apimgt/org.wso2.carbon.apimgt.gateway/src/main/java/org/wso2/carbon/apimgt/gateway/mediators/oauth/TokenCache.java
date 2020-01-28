package org.wso2.carbon.apimgt.gateway.mediators.oauth;

import org.wso2.carbon.apimgt.gateway.mediators.oauth.client.TokenResponse;

import java.util.HashMap;
import java.util.Map;

/**
 * Token Cache Implementation
 */
public class TokenCache {
    private static final TokenCache instance = new TokenCache();

    private final Map<String, TokenResponse> tokenMap = new HashMap<>();

    /**
     * Private Constructor
     */
    private TokenCache() {
    }

    /**
     * Get TokenCache Instance
     * @return
     */
    public static TokenCache getInstance() {
        return instance;
    }

    /**
     * Get token map
     * @return
     */
    public Map<String, TokenResponse> getTokenMap() {
        return tokenMap;
    }
}
