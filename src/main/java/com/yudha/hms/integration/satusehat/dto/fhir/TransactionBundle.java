package com.yudha.hms.integration.satusehat.dto.fhir;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * FHIR Transaction Bundle.
 * 
 * For submitting multiple resources in a single transaction.
 * 
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-20
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TransactionBundle {
    
    @JsonProperty("resourceType")
    @Builder.Default
    private String resourceType = "Bundle";
    
    @JsonProperty("type")
    @Builder.Default
    private String type = "transaction";
    
    @JsonProperty("entry")
    private List<Entry> entry;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Entry {
        @JsonProperty("fullUrl")
        private String fullUrl; // urn:uuid:...
        
        @JsonProperty("resource")
        private Object resource; // Any FHIR resource
        
        @JsonProperty("request")
        private Request request;
        
        @JsonProperty("response")
        private Response response; // Populated in response
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Request {
        @JsonProperty("method")
        private String method; // POST, PUT, GET, DELETE
        
        @JsonProperty("url")
        private String url; // Resource type or resource/id
        
        @JsonProperty("ifMatch")
        private String ifMatch;
        
        @JsonProperty("ifNoneExist")
        private String ifNoneExist;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Response {
        @JsonProperty("status")
        private String status; // HTTP status code
        
        @JsonProperty("location")
        private String location;
        
        @JsonProperty("etag")
        private String etag;
        
        @JsonProperty("lastModified")
        private String lastModified;
        
        @JsonProperty("outcome")
        private Object outcome; // OperationOutcome resource
    }
}
