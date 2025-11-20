package com.yudha.hms.integration.bpjs.dto.antreanrs;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * BPJS Antrean RS Task List Response.
 *
 * Response for getting list of task timestamps.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-20
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class TaskListResponse {

    @JsonProperty("metadata")
    private Metadata metadata;

    @JsonProperty("response")
    private Response response;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Metadata {
        @JsonProperty("code")
        private Integer code;

        @JsonProperty("message")
        private String message;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Response {
        @JsonProperty("list")
        private List<TaskItem> list;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class TaskItem {
        @JsonProperty("kodebooking")
        private String kodebooking;

        @JsonProperty("taskid")
        private Integer taskid;

        @JsonProperty("taskname")
        private String taskname;

        @JsonProperty("waktu")
        private String waktu;

        @JsonProperty("wakturs")
        private String wakturs;
    }
}
