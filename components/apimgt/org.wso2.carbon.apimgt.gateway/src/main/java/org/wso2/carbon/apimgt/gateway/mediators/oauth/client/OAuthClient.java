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

package org.wso2.carbon.apimgt.gateway.mediators.oauth.client;

import com.google.gson.Gson;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.wso2.carbon.apimgt.api.APIManagementException;
import org.wso2.carbon.apimgt.impl.APIConstants;
import org.wso2.carbon.apimgt.impl.utils.APIUtil;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class OAuthClient {
    private static final Log log = LogFactory.getLog(OAuthClient.class);
    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String CONTENT_TYPE_HEADER = "Content-Type";
    private static final String APPLICATION_X_WWW_FORM_URLENCODED = "application/x-www-form-urlencoded";

    public static TokenResponse generateToken(String url, String apiKey, String apiSecret,
            String username, String password, String grantType)
            throws IOException, APIManagementException {
        if(log.isDebugEnabled()) {
            log.debug("Initializing token generation request: [token-endpoint] " + url);
        }

        HttpPost httpPost = null;
        URL url_ = new URL(url);
        httpPost = new HttpPost(url);
        // Set authorization header
        String credentials = Base64.getEncoder().encodeToString((apiKey + ":" + apiSecret).getBytes());
        httpPost.setHeader(AUTHORIZATION_HEADER, "Basic " + credentials);
        httpPost.setHeader(CONTENT_TYPE_HEADER, APPLICATION_X_WWW_FORM_URLENCODED);

        try (CloseableHttpClient httpClient = (CloseableHttpClient) APIUtil
                .getHttpClient(url_.getPort(), url_.getProtocol())) {
            if (grantType.equals(APIConstants.OAuthConstants.CLIENT_CREDENTIALS)) {
                String clientCredGrantType = "grant_type=client_credentials";
                httpPost.setEntity(new StringEntity(clientCredGrantType));
            } else if (grantType.equals(APIConstants.OAuthConstants.PASSWORD)) {
                String passwordGrantType = "grant_type=password";
                httpPost.setEntity(new StringEntity(passwordGrantType));
                httpPost.setEntity(new StringEntity("username=" + username));
                httpPost.setEntity(new StringEntity("password=" + password));
            }

            try (CloseableHttpResponse response = httpClient.execute(httpPost)) {
                int responseCode = response.getStatusLine().getStatusCode();

                if (!(responseCode == HttpStatus.SC_OK)) {
                    throw new APIManagementException("Error while accessing the Token URL. Found http status " + response.getStatusLine());
                }
                BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), StandardCharsets.UTF_8));
                String inputLine;
                StringBuilder stringBuilder = new StringBuilder();

                while ((inputLine = reader.readLine()) != null) {
                    stringBuilder.append(inputLine);
                }

                TokenResponse tokenResponse = new Gson().fromJson(stringBuilder.toString(), TokenResponse.class);
                long currentTimeInSeconds = System.currentTimeMillis() / 1000;
                long expiryTimeInSeconds = currentTimeInSeconds + Long.parseLong(tokenResponse.getExpiresIn());
                tokenResponse.setValidTill(expiryTimeInSeconds);

                if (log.isDebugEnabled()) {
                    log.debug("Response: [status-code] " + responseCode + " [message] " + stringBuilder.toString());
                }
                return tokenResponse;
            } finally {
                httpPost.releaseConnection();
            }
        }
    }

}
