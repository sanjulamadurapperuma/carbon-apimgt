package org.wso2.carbon.apimgt.gateway.mediators.oauth.conf;

public class OAuthEndpoint {

    private String id;
    private String tokenApiUrl;
    private String apiKey;
    private String apiSecret;
    private String username;
    private String password;
    private String grantType;
    private int tokenRefreshInterval;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTokenApiUrl() {
        return tokenApiUrl;
    }

    public void setTokenApiUrl(String tokenApiUrl) {
        this.tokenApiUrl = tokenApiUrl;
    }

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public String getApiSecret() {
        return apiSecret;
    }

    public void setApiSecret(String apiSecret) {
        this.apiSecret = apiSecret;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getGrantType() {
        return grantType;
    }

    public void setGrantType(String grantType) {
        this.grantType = grantType;
    }

    public int getTokenRefreshInterval() {
        return tokenRefreshInterval;
    }

    public void setTokenRefreshInterval(int tokenRefreshInterval) {
        this.tokenRefreshInterval = tokenRefreshInterval;
    }
}
