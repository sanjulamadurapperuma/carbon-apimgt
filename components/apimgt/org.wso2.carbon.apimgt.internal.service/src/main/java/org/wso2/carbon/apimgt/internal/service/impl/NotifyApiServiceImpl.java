package org.wso2.carbon.apimgt.internal.service.impl;

import org.apache.cxf.jaxrs.ext.MessageContext;
import org.wso2.carbon.apimgt.api.APIManagementException;
import org.wso2.carbon.apimgt.internal.service.NotifyApiService;
import org.wso2.carbon.apimgt.notification.NotificationEventService;
import org.wso2.carbon.context.PrivilegedCarbonContext;

import java.util.Collections;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;

public class NotifyApiServiceImpl implements NotifyApiService {

    @Override
    public Response notifyPost(String xWSO2KEYManager, String body, MessageContext messageContext)
            throws APIManagementException {

        NotificationEventService notificationEventService =
                (NotificationEventService) PrivilegedCarbonContext.getThreadLocalCarbonContext()
                        .getOSGiService(NotificationEventService.class, new Hashtable<>());
        Map<String, List<String>> requestHeaders = messageContext.getHttpHeaders().getRequestHeaders();
        notificationEventService.processEvent(xWSO2KEYManager,body, requestHeaders);
        return Response.ok().build();
    }
}
