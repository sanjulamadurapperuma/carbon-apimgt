package org.wso2.carbon.apimgt.rest.api.publisher.v1.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import javax.validation.constraints.*;


import io.swagger.annotations.*;
import java.util.Objects;

import javax.xml.bind.annotation.*;
import org.wso2.carbon.apimgt.rest.api.util.annotations.Scope;



public class APIEndpointSecurityDTO   {
  

@XmlType(name="TypeEnum")
@XmlEnum(String.class)
public enum TypeEnum {

    @XmlEnumValue("BASIC") BASIC(String.valueOf("BASIC")), @XmlEnumValue("DIGEST") DIGEST(String.valueOf("DIGEST")), @XmlEnumValue("OAUTH") OAUTH(String.valueOf("OAUTH"));


    private String value;

    TypeEnum (String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }

    public static TypeEnum fromValue(String v) {
        for (TypeEnum b : TypeEnum.values()) {
            if (String.valueOf(b.value).equals(v)) {
                return b;
            }
        }
        return null;
    }
}

    private TypeEnum type = null;
    private String username = null;
    private String password = null;
    private String grantType = null;
    private String httpMethod = null;
    private String tokenUrl = null;
    private String apiKey = null;
    private String apiSecret = null;

  /**
   * Accepts one of the following, basic, digest or oauth.
   **/
  public APIEndpointSecurityDTO type(TypeEnum type) {
    this.type = type;
    return this;
  }

  
  @ApiModelProperty(example = "basic", value = "Accepts one of the following, basic, digest or oauth.")
  @JsonProperty("type")
  public TypeEnum getType() {
    return type;
  }
  public void setType(TypeEnum type) {
    this.type = type;
  }

  /**
   **/
  public APIEndpointSecurityDTO username(String username) {
    this.username = username;
    return this;
  }

  
  @ApiModelProperty(example = "admin", value = "")
  @JsonProperty("username")
  public String getUsername() {
    return username;
  }
  public void setUsername(String username) {
    this.username = username;
  }

  /**
   **/
  public APIEndpointSecurityDTO password(String password) {
    this.password = password;
    return this;
  }

  
  @ApiModelProperty(example = "password", value = "")
  @JsonProperty("password")
  public String getPassword() {
    return password;
  }
  public void setPassword(String password) {
    this.password = password;
  }

  /**
   **/
  public APIEndpointSecurityDTO grantType(String grantType) {
    this.grantType = grantType;
    return this;
  }

  
  @ApiModelProperty(example = "client_credentials", value = "")
  @JsonProperty("grantType")
  public String getGrantType() {
    return grantType;
  }
  public void setGrantType(String grantType) {
    this.grantType = grantType;
  }

  /**
   **/
  public APIEndpointSecurityDTO httpMethod(String httpMethod) {
    this.httpMethod = httpMethod;
    return this;
  }

  
  @ApiModelProperty(example = "get", value = "")
  @JsonProperty("httpMethod")
  public String getHttpMethod() {
    return httpMethod;
  }
  public void setHttpMethod(String httpMethod) {
    this.httpMethod = httpMethod;
  }

  /**
   **/
  public APIEndpointSecurityDTO tokenUrl(String tokenUrl) {
    this.tokenUrl = tokenUrl;
    return this;
  }

  
  @ApiModelProperty(example = "http://localhost:8244/token", value = "")
  @JsonProperty("tokenUrl")
  public String getTokenUrl() {
    return tokenUrl;
  }
  public void setTokenUrl(String tokenUrl) {
    this.tokenUrl = tokenUrl;
  }

  /**
   **/
  public APIEndpointSecurityDTO apiKey(String apiKey) {
    this.apiKey = apiKey;
    return this;
  }

  
  @ApiModelProperty(example = "by2gPAePag6N165_NVnKB7cI8iAa", value = "")
  @JsonProperty("apiKey")
  public String getApiKey() {
    return apiKey;
  }
  public void setApiKey(String apiKey) {
    this.apiKey = apiKey;
  }

  /**
   **/
  public APIEndpointSecurityDTO apiSecret(String apiSecret) {
    this.apiSecret = apiSecret;
    return this;
  }

  
  @ApiModelProperty(example = "3KQt5bvgoesIS7TRj58IsoqlIgIa", value = "")
  @JsonProperty("apiSecret")
  public String getApiSecret() {
    return apiSecret;
  }
  public void setApiSecret(String apiSecret) {
    this.apiSecret = apiSecret;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    APIEndpointSecurityDTO apIEndpointSecurity = (APIEndpointSecurityDTO) o;
    return Objects.equals(type, apIEndpointSecurity.type) &&
        Objects.equals(username, apIEndpointSecurity.username) &&
        Objects.equals(password, apIEndpointSecurity.password) &&
        Objects.equals(grantType, apIEndpointSecurity.grantType) &&
        Objects.equals(httpMethod, apIEndpointSecurity.httpMethod) &&
        Objects.equals(tokenUrl, apIEndpointSecurity.tokenUrl) &&
        Objects.equals(apiKey, apIEndpointSecurity.apiKey) &&
        Objects.equals(apiSecret, apIEndpointSecurity.apiSecret);
  }

  @Override
  public int hashCode() {
    return Objects.hash(type, username, password, grantType, httpMethod, tokenUrl, apiKey, apiSecret);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class APIEndpointSecurityDTO {\n");
    
    sb.append("    type: ").append(toIndentedString(type)).append("\n");
    sb.append("    username: ").append(toIndentedString(username)).append("\n");
    sb.append("    password: ").append(toIndentedString(password)).append("\n");
    sb.append("    grantType: ").append(toIndentedString(grantType)).append("\n");
    sb.append("    httpMethod: ").append(toIndentedString(httpMethod)).append("\n");
    sb.append("    tokenUrl: ").append(toIndentedString(tokenUrl)).append("\n");
    sb.append("    apiKey: ").append(toIndentedString(apiKey)).append("\n");
    sb.append("    apiSecret: ").append(toIndentedString(apiSecret)).append("\n");
    sb.append("}");
    return sb.toString();
  }

  /**
   * Convert the given object to string with each line indented by 4 spaces
   * (except the first line).
   */
  private String toIndentedString(java.lang.Object o) {
    if (o == null) {
      return "null";
    }
    return o.toString().replace("\n", "\n    ");
  }
}

