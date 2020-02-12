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
import org.apache.http.client.methods.HttpGet;
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
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class OAuthClient {
    private static final Log log = LogFactory.getLog(OAuthClient.class);
    private static final String UTF_8 = "UTF-8";
    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String CONTENT_TYPE_HEADER = "Content-Type";
    private static final String APPLICATION_X_WWW_FORM_URLENCODED = "application/x-www-form-urlencoded";
    private static final String CLIENT_CRED_GRANT_TYPE = "grant_type=client_credentials";
    private static final String PASSWORD_GRANT_TYPE = "grant_type=password";

    public static TokenResponse generateToken(String httpMethod, String url, String apiKey, String apiSecret,
            String username, String password, String grantType)
            throws IOException, APIManagementException {
        if(log.isDebugEnabled()) {
            log.debug("Initializing token generation request: [token-endpoint] " + url);
        }

        URL url_;
        String credentials = Base64.getEncoder().encodeToString((apiKey + ":" + apiSecret).getBytes());

        if (httpMethod.equals("post")) {
            url_ = new URL(url);

            try (CloseableHttpClient httpClient = (CloseableHttpClient) APIUtil
                    .getHttpClient(url_.getPort(), url_.getProtocol())) {
                HttpPost httpPost = new HttpPost(url);
                // Set authorization header
                httpPost.setHeader(AUTHORIZATION_HEADER, "Basic " + credentials);
                httpPost.setHeader(CONTENT_TYPE_HEADER, APPLICATION_X_WWW_FORM_URLENCODED);
                if (grantType.equals(APIConstants.OAuthConstants.CLIENT_CREDENTIALS)) {
                    httpPost.setEntity(new StringEntity(CLIENT_CRED_GRANT_TYPE));
                } else if (grantType.equals(APIConstants.OAuthConstants.PASSWORD)) {
                    httpPost.setEntity(new StringEntity(PASSWORD_GRANT_TYPE + "&username="
                            + username + "&password=" + password));
                }

                try (CloseableHttpResponse response = httpClient.execute(httpPost)) {
                    return getTokenResponse(response);
                } finally {
                    httpPost.releaseConnection();
                }
            }
        } else if (httpMethod.equals("get")) {
            String query;
            if (grantType.equals(APIConstants.OAuthConstants.CLIENT_CREDENTIALS)) {
                query = "?" + CLIENT_CRED_GRANT_TYPE;
                url += query;
                url_ = new URL(url);

                try (CloseableHttpClient httpClient = (CloseableHttpClient) APIUtil
                        .getHttpClient(url_.getPort(), url_.getProtocol())) {
                    HttpGet httpGet = new HttpGet(url);
                    // Set authorization header
                    httpGet.setHeader(AUTHORIZATION_HEADER, "Basic " + credentials);
                    httpGet.setHeader(CONTENT_TYPE_HEADER, APPLICATION_X_WWW_FORM_URLENCODED);

                    try (CloseableHttpResponse response = httpClient.execute(httpGet)) {
                        return getTokenResponse(response);
                    }
                }
            } else if (grantType.equals(APIConstants.OAuthConstants.PASSWORD)) {
                query = String.format(PASSWORD_GRANT_TYPE + "&username=%s&password=%s",
                        URLEncoder.encode(username, UTF_8), URLEncoder.encode(password, UTF_8));
                url += query;
                url_ = new URL(url);

                try (CloseableHttpClient httpClient = (CloseableHttpClient) APIUtil
                        .getHttpClient(url_.getPort(), url_.getProtocol())) {
                    HttpGet httpGet = new HttpGet(url);
                    // Set authorization header
                    httpGet.setHeader(AUTHORIZATION_HEADER, "Basic " + credentials);
                    httpGet.setHeader(CONTENT_TYPE_HEADER, APPLICATION_X_WWW_FORM_URLENCODED);

                    try (CloseableHttpResponse response = httpClient.execute(httpGet)) {
                        return getTokenResponse(response);
                    }
                }
            }
        }
        return null;
    }

    private static TokenResponse getTokenResponse(CloseableHttpResponse response)
            throws APIManagementException, IOException {
        int responseCode = response.getStatusLine().getStatusCode();

        if (!(responseCode == HttpStatus.SC_OK)) {
            throw new APIManagementException("Error while accessing the Token URL. "
                    + "Found http status " + response.getStatusLine());
        }
        BufferedReader reader = new BufferedReader(new InputStreamReader(response
                .getEntity().getContent(), StandardCharsets.UTF_8));
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
            log.debug("Response: [status-code] " + responseCode + " [message] "
                    + stringBuilder.toString());
        }
        return tokenResponse;
    }

}
