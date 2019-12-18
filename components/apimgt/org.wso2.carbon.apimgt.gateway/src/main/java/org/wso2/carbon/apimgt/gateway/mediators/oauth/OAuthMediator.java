package org.wso2.carbon.apimgt.gateway.mediators.oauth;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.synapse.ManagedLifecycle;
import org.apache.synapse.MessageContext;
import org.apache.synapse.core.SynapseEnvironment;
import org.apache.synapse.mediators.AbstractMediator;

public class OAuthMediator extends AbstractMediator implements ManagedLifecycle {

    private static final Log log = LogFactory.getLog(OAuthMediator.class);

    private String endpointId;

    @Override public void init(SynapseEnvironment synapseEnvironment) {
        //TODO
    }

    @Override public void destroy() {
        //TODO
    }

    @Override public boolean mediate(MessageContext messageContext) {
        //TODO
        log.debug("--> OAuth Mediator Invoked");
        String accessToken;
        return false;
    }

    public String getEndpointId() {
        return endpointId;
    }

    public void setEndpointId(String endpointId) {
        this.endpointId = endpointId;
    }
}
