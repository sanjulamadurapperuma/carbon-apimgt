package org.wso2.carbon.apimgt.gateway.mediators.oauth.jms;

import org.wso2.carbon.apimgt.gateway.mediators.oauth.conf.OAuthEndpoint;

public class Message {

    public final String type;
    public final OAuthEndpoint oAuthEndpoint;

    public Message(String type, OAuthEndpoint oAuthEndpoint) {
        this.type = type;
        this.oAuthEndpoint = oAuthEndpoint;
    }

}
