package org.wso2.carbon.apimgt.gateway.mediators.oauth.jms;

import org.wso2.carbon.apimgt.gateway.mediators.oauth.conf.OAuthEndpoint;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.*;

public class OAuthPubSub implements Runnable {

    private int NUMBER_OF_THREADS = 1;
    private ExecutorService executorService;
    private final BlockingQueue<Message> messageQueue;
    private Map<String, Set<OAuthMessageListener>> listeners;
    private static OAuthPubSub _instance;

    public static OAuthPubSub getInstance() {
        if (_instance == null) {
            synchronized (OAuthPubSub.class) {
                if (_instance == null) {
                    _instance = new OAuthPubSub();
                }
            }
        }
        return _instance;
    }

    private OAuthPubSub() {
        listeners = new ConcurrentHashMap<String, Set<OAuthMessageListener>>();
        messageQueue = new LinkedBlockingQueue<Message>();
        executorService = Executors.newFixedThreadPool(NUMBER_OF_THREADS);
        executorService.submit(this);
    }

    public void addListener(String type, OAuthMessageListener oAuthMessageListener) {
        add(type, oAuthMessageListener);
    }

    public void addListeners(OAuthMessageListener oAuthMessageListener, String... types) {
        for (String type : types) {
            add(type, oAuthMessageListener);
        }
    }

    private void add(String type, OAuthMessageListener oAuthMessageListener) {
        Set<OAuthMessageListener> list;
        list = listeners.get(type);
        if (list == null) {
            synchronized (this) {
                if ((list = listeners.get(type)) == null) {
                    list = new CopyOnWriteArraySet<OAuthMessageListener>();
                    listeners.put(type, list);
                }
            }
        }
        list.add(oAuthMessageListener);
    }

    public void removeListener(String type, OAuthMessageListener oAuthMessageListener) {
        remove(type, oAuthMessageListener);
    }

    public void removeListeners(OAuthMessageListener oAuthMessageListener, String... types) {
        for (String type : types) {
            remove(type, oAuthMessageListener);
        }
    }

    public void remove(String type, OAuthMessageListener oAuthMessageListener) {
        Set<OAuthMessageListener> oAuthMessageListenerSet = null;
        oAuthMessageListenerSet = listeners.get(type);
        if (oAuthMessageListenerSet != null) {
            oAuthMessageListenerSet.remove(oAuthMessageListener);
        }
    }

    public boolean publish(String type, OAuthEndpoint oAuthEndpoint) {
        Set<OAuthMessageListener> oAuthMessageListenerSet = listeners.get(type);
        if (oAuthMessageListenerSet != null && oAuthMessageListenerSet.size() >= 0) {
            messageQueue.add(new Message(type, oAuthEndpoint));
            return true;
        }
        return false;
    }

    @Override
    public void run() {
        Message message;
        while (true) {
            try {
                message = messageQueue.take();
            } catch (InterruptedException e) {
//                e.printStackTrace();
                // TODO - Improve this logic
                continue;
            }

            String type = message.type;
            OAuthEndpoint oAuthEndpoint = message.oAuthEndpoint;

            Set<OAuthMessageListener> list = listeners.get(type);

            if (list == null || list.isEmpty()) {
                // TODO - Handle this logic properly
                continue;
            }

            for (OAuthMessageListener listener : list) {
                listener.onEventReceived(type, oAuthEndpoint);
            }
        }
    }
}
