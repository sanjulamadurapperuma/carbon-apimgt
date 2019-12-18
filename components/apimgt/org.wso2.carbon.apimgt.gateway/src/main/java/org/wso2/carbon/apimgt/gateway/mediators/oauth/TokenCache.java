package org.wso2.carbon.apimgt.gateway.mediators.oauth;

import java.util.HashMap;
import java.util.Map;

/**
 * Token Cache Implementation
 */
public class TokenCache {
    private static final TokenCache instance = new TokenCache();

    private final Map<String, String> tokenMap = new HashMap<>();

    /**
     * Private Constructor
     */
    private TokenCache() {

    }

    /**
     * Get Token Cache Instance
     * @return
     */
    public static TokenCache getInstance() {
        return instance;
    }

    /**
     * Get Token Map.
     * @return
     */
    public Map<String, String> getTokenMap() {
        return tokenMap;
    }
}
