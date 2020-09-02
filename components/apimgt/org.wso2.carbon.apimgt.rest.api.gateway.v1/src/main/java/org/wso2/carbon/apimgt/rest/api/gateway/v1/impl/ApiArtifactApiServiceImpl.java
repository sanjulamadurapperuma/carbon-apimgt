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

package org.wso2.carbon.apimgt.rest.api.gateway.v1.impl;

import org.apache.axis2.AxisFault;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.cxf.jaxrs.ext.MessageContext;
import org.json.JSONArray;
import org.json.JSONObject;
import org.wso2.carbon.apimgt.api.gateway.GatewayAPIDTO;
import org.wso2.carbon.apimgt.api.gateway.GatewayContentDTO;
import org.wso2.carbon.apimgt.gateway.InMemoryAPIDeployer;
import org.wso2.carbon.apimgt.gateway.utils.EndpointAdminServiceProxy;
import org.wso2.carbon.apimgt.gateway.utils.LocalEntryServiceProxy;
import org.wso2.carbon.apimgt.gateway.utils.SequenceAdminServiceProxy;
import org.wso2.carbon.apimgt.impl.APIConstants;
import org.wso2.carbon.apimgt.impl.gatewayartifactsynchronizer.exception.ArtifactSynchronizerException;
import org.wso2.carbon.apimgt.rest.api.gateway.v1.ApiArtifactApiService;
import org.wso2.carbon.apimgt.rest.api.util.utils.RestApiUtil;
import org.wso2.carbon.endpoint.EndpointAdminException;

import java.util.Map;

import javax.ws.rs.core.Response;

public class ApiArtifactApiServiceImpl implements ApiArtifactApiService {

    private static final Log log = LogFactory.getLog(ApiArtifactApiServiceImpl.class);
    private boolean debugEnabled = log.isDebugEnabled();

    @Override
    public Response apiArtifactGet(String apiName, String version, String tenantDomain,
                                   MessageContext messageContext) {

        InMemoryAPIDeployer inMemoryApiDeployer = new InMemoryAPIDeployer();
        if (tenantDomain == null) {
            tenantDomain = APIConstants.SUPER_TENANT_DOMAIN;
        }
        GatewayAPIDTO gatewayAPIDTO = null;
        JSONObject responseObj = new JSONObject();
        try {
            Map<String, String> apiAttributes = inMemoryApiDeployer.getGatewayAPIAttributes(apiName, version,
                    tenantDomain);
            String apiId = apiAttributes.get(APIConstants.GatewayArtifactSynchronizer.API_ID);
            String label = apiAttributes.get(APIConstants.GatewayArtifactSynchronizer.LABEL);

            if (label == null) {
                return Response.status(Response.Status.BAD_REQUEST).entity(apiName + " is not deployed in the Gateway")
                        .build();
            }

            gatewayAPIDTO = inMemoryApiDeployer.getAPIArtifact(apiId, label);
            if (debugEnabled) {
                log.debug("Retrieved Artifacts for " + apiName + " from eventhub");
            }
        } catch (ArtifactSynchronizerException e) {
            String errorMessage = "Error in fetching artifacts from storage";
            log.error(errorMessage, e);
            RestApiUtil.handleInternalServerError(errorMessage, e, log);
        }

        if (gatewayAPIDTO != null) {
            try {
                JSONArray endPointArray = new JSONArray();
                JSONArray unDeployedEndPointArray = new JSONArray();
                if (gatewayAPIDTO.getEndpointEntriesToBeAdd() != null) {
                    EndpointAdminServiceProxy endpointAdminServiceProxy = new EndpointAdminServiceProxy
                            (gatewayAPIDTO.getTenantDomain());
                    for (GatewayContentDTO gatewayEndpoint : gatewayAPIDTO.getEndpointEntriesToBeAdd()) {
                        if (endpointAdminServiceProxy.isEndpointExist(gatewayEndpoint.getName())) {
                            endPointArray.put(endpointAdminServiceProxy.getEndpoints(gatewayEndpoint.getName()));
                        } else {
                            log.error(gatewayEndpoint.getName() + " was not deployed in the gateway");
                            unDeployedEndPointArray.put(gatewayEndpoint.getContent());
                        }
                    }
                }
                responseObj.put("Deployed Endpoints", endPointArray);
                responseObj.put("UnDeployed Endpoints", unDeployedEndPointArray);

                JSONArray localEntryArray = new JSONArray();
                JSONArray UnDeploeydLocalEntryArray = new JSONArray();
                if (gatewayAPIDTO.getLocalEntriesToBeAdd() != null) {
                    LocalEntryServiceProxy localEntryServiceProxy = new
                            LocalEntryServiceProxy(gatewayAPIDTO.getTenantDomain());
                    for (GatewayContentDTO localEntry : gatewayAPIDTO.getLocalEntriesToBeAdd()) {
                        if (localEntryServiceProxy.isEntryExists(localEntry.getName())) {
                            localEntryArray.put(localEntryServiceProxy.getEntry(localEntry.getName()));
                        } else {
                            log.error(localEntry.getName() + " was not deployed in the gateway");
                            UnDeploeydLocalEntryArray.put(localEntry.getContent());
                        }
                    }
                }
                responseObj.put("Deployed Local Entries", localEntryArray);
                responseObj.put("Undeployed Local Entries", UnDeploeydLocalEntryArray);

                JSONArray sequencesArray = new JSONArray();
                JSONArray undeployedsequencesArray = new JSONArray();
                if (gatewayAPIDTO.getSequenceToBeAdd() != null) {
                    SequenceAdminServiceProxy sequenceAdminServiceProxy =
                            new SequenceAdminServiceProxy(gatewayAPIDTO.getTenantDomain());
                    for (GatewayContentDTO sequence : gatewayAPIDTO.getSequenceToBeAdd()) {
                        if (sequenceAdminServiceProxy.isExistingSequence(sequence.getName())) {
                            sequencesArray.put(sequenceAdminServiceProxy.getSequence(sequence.getName()));
                        } else {
                            log.error(sequence.getName() + " was not deployed in the gateway");
                            undeployedsequencesArray.put(sequence.getContent());
                        }
                    }
                }
                responseObj.put("Deployed Sequences", sequencesArray);
                responseObj.put("Undeployed Sequences", undeployedsequencesArray);
            } catch (EndpointAdminException e) {
                String errorMessage = "Error in fetching deployed Endpoints from Synapse Configuration";
                log.error(errorMessage, e);
                RestApiUtil.handleInternalServerError(errorMessage, e, log);
            } catch (AxisFault e) {
                String errorMessage = "Error in fetching deployed artifacts from Synapse Configuration";
                log.error(errorMessage, e);
                RestApiUtil.handleInternalServerError(errorMessage, e, log);
            }

            String responseStringObj = String.valueOf(responseObj);
            return Response.ok().entity(responseStringObj).build();
        } else {
            return Response.serverError().entity("Unexpected error occurred").build();
        }
    }
}
