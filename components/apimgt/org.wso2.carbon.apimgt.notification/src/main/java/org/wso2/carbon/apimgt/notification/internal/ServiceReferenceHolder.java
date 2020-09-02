/*
 *  Copyright (c) 2020, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 *  WSO2 Inc. licenses this file to you under the Apache License,
 *  Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.wso2.carbon.apimgt.notification.internal;

import org.wso2.carbon.apimgt.impl.keymgt.KeyManagerEventHandler;
import org.wso2.carbon.event.stream.core.EventStreamService;

import java.util.HashMap;
import java.util.Map;

/**
 * Service holder class to keep osgi references.
 */
public class ServiceReferenceHolder {

    private static final ServiceReferenceHolder instance = new ServiceReferenceHolder();
    private Map<String, KeyManagerEventHandler> keyManagerEventHandlerMap = new HashMap<>();
    private EventStreamService eventStreamService;

    private ServiceReferenceHolder() {

    }

    public static ServiceReferenceHolder getInstance() {

        return instance;
    }

    public KeyManagerEventHandler getKeyManagerEventHandlerByType(String type) {

        return keyManagerEventHandlerMap.get(type);
    }

    public void addKeyManagerEventHandler(String type, KeyManagerEventHandler keyManagerEventHandler) {

        keyManagerEventHandlerMap.put(type, keyManagerEventHandler);

    }

    public void removeKeyManagerEventHandlers(String type) {

        keyManagerEventHandlerMap.remove(type);

    }

    public void setEventStreamService(EventStreamService eventStreamService) {

        this.eventStreamService = eventStreamService;
    }

    public EventStreamService getEventStreamService() {

        return eventStreamService;
    }
}
