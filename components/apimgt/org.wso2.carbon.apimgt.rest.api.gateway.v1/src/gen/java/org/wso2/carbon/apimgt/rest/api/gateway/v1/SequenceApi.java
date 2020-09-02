package org.wso2.carbon.apimgt.rest.api.gateway.v1;

import org.wso2.carbon.apimgt.rest.api.gateway.v1.dto.ErrorDTO;
import org.wso2.carbon.apimgt.rest.api.gateway.v1.dto.SequencesDTO;
import org.wso2.carbon.apimgt.rest.api.gateway.v1.SequenceApiService;
import org.wso2.carbon.apimgt.rest.api.gateway.v1.impl.SequenceApiServiceImpl;
import org.wso2.carbon.apimgt.api.APIManagementException;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import javax.inject.Inject;

import io.swagger.annotations.*;
import java.io.InputStream;

import org.apache.cxf.jaxrs.ext.MessageContext;
import org.apache.cxf.jaxrs.ext.multipart.Attachment;
import org.apache.cxf.jaxrs.ext.multipart.Multipart;

import java.util.Map;
import java.util.List;
import javax.validation.constraints.*;
@Path("/sequence")

@Api(description = "the sequence API")
@Consumes({ "application/json" })
@Produces({ "application/json" })


public class SequenceApi  {

  @Context MessageContext securityContext;

SequenceApiService delegate = new SequenceApiServiceImpl();


    @GET
    
    @Consumes({ "application/json" })
    @Produces({ "application/json" })
    @ApiOperation(value = "Get sequences from the storage", notes = "This operation is used to get the API sequence from the storage. If the Tenant domain is not provided carbon.super will be picked as the Tenant domain. ", response = SequencesDTO.class, authorizations = {
        @Authorization(value = "OAuth2Security", scopes = {
            @AuthorizationScope(scope = "apim:api_publish", description = "Publish API")
        })
    }, tags={ "Get API Artifacts" })
    @ApiResponses(value = { 
        @ApiResponse(code = 200, message = "OK. Sequences for the API successfully retrieved from the storage. ", response = SequencesDTO.class),
        @ApiResponse(code = 202, message = "Accepted. The request has been accepted. ", response = SequencesDTO.class),
        @ApiResponse(code = 400, message = "Bad Request. Invalid request or validation error ", response = ErrorDTO.class),
        @ApiResponse(code = 404, message = "Not Found. Requested API does not exist. ", response = ErrorDTO.class),
        @ApiResponse(code = 412, message = "Precondition Failed. The request has not been performed because one of the preconditions is not met. ", response = ErrorDTO.class) })
    public Response sequenceGet( @NotNull @ApiParam(value = "Name of the API ",required=true)  @QueryParam("apiName") String apiName,  @NotNull @ApiParam(value = "version of the API ",required=true)  @QueryParam("version") String version,  @ApiParam(value = "Tenant Domain of the API ")  @QueryParam("tenantDomain") String tenantDomain) throws APIManagementException{
        return delegate.sequenceGet(apiName, version, tenantDomain, securityContext);
    }
}
