package org.wso2.carbon.apimgt.rest.api.admin.v1;

import org.wso2.carbon.apimgt.rest.api.admin.v1.dto.ErrorDTO;
import java.io.File;
import org.wso2.carbon.apimgt.rest.api.admin.v1.TenantThemeApiService;
import org.wso2.carbon.apimgt.rest.api.admin.v1.impl.TenantThemeApiServiceImpl;
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
@Path("/tenant-theme")

@Api(description = "the tenant-theme API")
@Consumes({ "application/json" })
@Produces({ "application/json" })


public class TenantThemeApi  {

  @Context MessageContext securityContext;

TenantThemeApiService delegate = new TenantThemeApiServiceImpl();


    @GET
    
    @Consumes({ "application/json" })
    @Produces({ "application/zip" })
    @ApiOperation(value = "Export a DevPortal Tenant Theme", notes = "This operation can be used to export a DevPortal tenant theme as a zip file. ", response = File.class, authorizations = {
        @Authorization(value = "OAuth2Security", scopes = {
            @AuthorizationScope(scope = "apim:tenant_theme_manage", description = "Manage tenant themes"),
            @AuthorizationScope(scope = "apim:admin", description = "Manage all admin operations")
        })
    }, tags={ "Tenant Theme",  })
    @ApiResponses(value = { 
        @ApiResponse(code = 200, message = "OK. Tenant Theme Exported Successfully. ", response = File.class),
        @ApiResponse(code = 403, message = "Forbidden. Not Authorized to export. ", response = ErrorDTO.class),
        @ApiResponse(code = 404, message = "Not Found. Requested tenant theme does not exist. ", response = ErrorDTO.class),
        @ApiResponse(code = 500, message = "Internal Server Error. Error in exporting tenant theme. ", response = ErrorDTO.class) })
    public Response exportTenantTheme() throws APIManagementException{
        return delegate.exportTenantTheme(securityContext);
    }

    @PUT
    
    @Consumes({ "multipart/form-data" })
    @Produces({ "application/json" })
    @ApiOperation(value = "Import a DevPortal Tenant Theme", notes = "This operation can be used to import a DevPortal tenant theme. ", response = Void.class, authorizations = {
        @Authorization(value = "OAuth2Security", scopes = {
            @AuthorizationScope(scope = "apim:tenant_theme_manage", description = "Manage tenant themes"),
            @AuthorizationScope(scope = "apim:admin", description = "Manage all admin operations")
        })
    }, tags={ "Tenant Theme" })
    @ApiResponses(value = { 
        @ApiResponse(code = 200, message = "Ok. Tenant Theme Imported Successfully. ", response = Void.class),
        @ApiResponse(code = 403, message = "Forbidden. Not Authorized to import. ", response = ErrorDTO.class),
        @ApiResponse(code = 413, message = "Payload Too Large. Tenant Theme file size exceeds the allowed limit. ", response = Void.class),
        @ApiResponse(code = 500, message = "Internal Server Error. Error in importing Theme. ", response = ErrorDTO.class) })
    public Response importTenantTheme( @Multipart(value = "file") InputStream fileInputStream, @Multipart(value = "file" ) Attachment fileDetail) throws APIManagementException{
        return delegate.importTenantTheme(fileInputStream, fileDetail, securityContext);
    }
}
