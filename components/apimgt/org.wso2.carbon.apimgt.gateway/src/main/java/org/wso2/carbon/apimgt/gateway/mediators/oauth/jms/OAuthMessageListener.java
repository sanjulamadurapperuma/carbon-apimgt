package org.wso2.carbon.apimgt.gateway.mediators.oauth.jms;

import org.wso2.carbon.apimgt.gateway.mediators.oauth.conf.OAuthEndpoint;

public interface OAuthMessageListener {
    void onEventReceived(String type, OAuthEndpoint oAuthEndpoint);
}
