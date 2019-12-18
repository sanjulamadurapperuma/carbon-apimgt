package org.wso2.carbon.apimgt.gateway.mediators.oauth;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;

/**
 * OAuth Token Generator Token Executor
 */
public class TokenGeneratorScheduledExecutor {
    private static final Log log = LogFactory.getLog(TokenGeneratorScheduledExecutor.class);

    private ScheduledExecutorService scheduledExecutorService;

    /**
     * Initialize OAuth client scheduled executor
     */
    public TokenGeneratorScheduledExecutor() {
        this.scheduledExecutorService = new ScheduledThreadPoolExecutor(5);
    }

    public void schedule() {

    }
}
