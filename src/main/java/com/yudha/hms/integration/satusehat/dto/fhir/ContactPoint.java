package com.yudha.hms.integration.satusehat.dto.fhir;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * FHIR ContactPoint Data Type.
 *
 * Details for all kinds of technology-mediated contact points for a person or organization.
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
public class ContactPoint {

    @JsonProperty("system")
    private String system; // phone | fax | email | pager | url | sms | other

    @JsonProperty("value")
    private String value; // The actual contact point details

    @JsonProperty("use")
    private String use; // home | work | temp | old | mobile

    @JsonProperty("rank")
    private Integer rank; // Specify preferred order of use (1 = highest)

    @JsonProperty("period")
    private Period period;

    /**
     * Create mobile phone contact
     */
    public static ContactPoint createMobilePhone(String phoneNumber) {
        return ContactPoint.builder()
            .system("phone")
            .value(phoneNumber)
            .use("mobile")
            .build();
    }

    /**
     * Create email contact
     */
    public static ContactPoint createEmail(String email) {
        return ContactPoint.builder()
            .system("email")
            .value(email)
            .use("home")
            .build();
    }

    /**
     * Create home phone contact
     */
    public static ContactPoint createHomePhone(String phoneNumber) {
        return ContactPoint.builder()
            .system("phone")
            .value(phoneNumber)
            .use("home")
            .build();
    }
}
