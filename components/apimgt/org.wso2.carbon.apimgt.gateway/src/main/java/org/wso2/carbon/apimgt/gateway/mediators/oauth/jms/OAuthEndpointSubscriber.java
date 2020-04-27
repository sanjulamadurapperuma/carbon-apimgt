package org.wso2.carbon.apimgt.gateway.mediators.oauth.jms;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.apimgt.api.APIManagementException;
import org.wso2.carbon.apimgt.gateway.mediators.oauth.TokenGeneratorScheduledExecutor;
import org.wso2.carbon.apimgt.gateway.mediators.oauth.conf.OAuthEndpoint;

import java.io.IOException;

public class OAuthEndpointSubscriber implements OAuthMessageListener {

    private static final Log log = LogFactory.getLog(OAuthEndpointSubscriber.class);
    private static final String TOPIC_OAUTH_ENDPOINT = "OAuthEndpoint";
    private static OAuthPubSub oAuthPubSub = OAuthPubSub.getInstance();

    public void subscribe() {
        oAuthPubSub.addListener(TOPIC_OAUTH_ENDPOINT, this);
    }

    @Override
    public void onEventReceived(String type, OAuthEndpoint oAuthEndpoint) {
        if (TOPIC_OAUTH_ENDPOINT.equals(type)) {
            try {
                TokenGeneratorScheduledExecutor.generateToken(oAuthEndpoint);
            } catch (IOException e) {
                log.error("Error occurred in HTTP client when trying to request for a token: ", e);
            } catch (APIManagementException e) {
                log.error("Error occurred while parsing the token response from the backend: ", e);
            }
        }
    }
}
