/*
 *
 *  Copyright (c) 2019, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */
package org.wso2.carbon.apimgt.api.model;

import org.json.simple.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class EndpointSecurity {

    private String password = null;

    private String type = null;

    private boolean enabled = false;

    private String username = null;

    private String grantType = null;

    private String tokenUrl = null;

    private String apiKey = null;

    private String apiSecret = null;

    private String customParameters = null;

    private Map additionalProperties = new HashMap();

    public String getPassword() {

        return password;
    }

    public void setPassword(String password) {

        this.password = password;
    }

    public String getType() {

        return type;
    }

    public void setType(String type) {

        this.type = type;
    }

    public boolean isEnabled() {

        return enabled;
    }

    public void setEnabled(boolean enabled) {

        this.enabled = enabled;
    }

    public String getUsername() {

        return username;
    }

    public void setUsername(String username) {

        this.username = username;
    }

    public String getGrantType() {
        return grantType;
    }

    public void setGrantType(String grantType) {
        this.grantType = grantType;
    }

    public String getTokenUrl() {
        return tokenUrl;
    }

    public void setTokenUrl(String tokenUrl) {
        this.tokenUrl = tokenUrl;
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

    public String getCustomParameters() {
        return customParameters;
    }

    public void setCustomParameters(String customParameters) {
        this.customParameters = customParameters;
    }

    public Map getAdditionalProperties() {

        return additionalProperties;
    }

    public void setAdditionalProperties(Map additionalProperties) {

        this.additionalProperties = additionalProperties;
    }

    @Override
    public String toString() {
        return "EndpointSecurity{" + "password='" + password + '\'' + ", type='" + type + '\'' + ", enabled=" + enabled
                + ", username='" + username + '\'' + ", grantType='" + grantType + '\'' + ", tokenUrl='" + tokenUrl
                + '\'' + ", apiKey='" + apiKey + '\'' + ", apiSecret='" + apiSecret + '\'' + ", customParameters="
                + customParameters + ", additionalProperties=" + additionalProperties + '}';
    }
}
