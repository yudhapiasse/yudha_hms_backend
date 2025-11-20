package com.yudha.hms.integration.satusehat.dto.fhir;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * FHIR Address Data Type.
 *
 * An address expressed using postal conventions (as opposed to GPS or other location definition formats).
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
public class Address {

    @JsonProperty("use")
    private String use; // home | work | temp | old | billing

    @JsonProperty("type")
    private String type; // postal | physical | both

    @JsonProperty("text")
    private String text; // Full address as displayed

    @JsonProperty("line")
    private List<String> line; // Street name, number, direction & P.O. Box etc.

    @JsonProperty("city")
    private String city; // Kota/Kabupaten

    @JsonProperty("district")
    private String district; // Kecamatan

    @JsonProperty("state")
    private String state; // Provinsi

    @JsonProperty("postalCode")
    private String postalCode;

    @JsonProperty("country")
    private String country; // Country (ISO 3166 2 or 3 letter code)

    @JsonProperty("period")
    private Period period;

    @JsonProperty("extension")
    private List<Extension> extension; // Administrative codes

    /**
     * Create home address with administrative codes
     */
    public static Address createHomeAddress(
        String fullAddress,
        List<String> streetLines,
        String city,
        String district,
        String state,
        String postalCode,
        String provinceCode,
        String cityCode,
        String districtCode,
        String villageCode
    ) {
        Address address = Address.builder()
            .use("home")
            .type("both")
            .text(fullAddress)
            .line(streetLines)
            .city(city)
            .district(district)
            .state(state)
            .postalCode(postalCode)
            .country("ID")
            .build();

        // Add administrative codes extension
        if (provinceCode != null || cityCode != null || districtCode != null || villageCode != null) {
            Extension adminCodeExt = Extension.builder()
                .url("https://fhir.kemkes.go.id/r4/StructureDefinition/administrativeCode")
                .extension(List.of(
                    provinceCode != null ? Extension.builder()
                        .url("province")
                        .valueCode(provinceCode)
                        .build() : null,
                    cityCode != null ? Extension.builder()
                        .url("city")
                        .valueCode(cityCode)
                        .build() : null,
                    districtCode != null ? Extension.builder()
                        .url("district")
                        .valueCode(districtCode)
                        .build() : null,
                    villageCode != null ? Extension.builder()
                        .url("village")
                        .valueCode(villageCode)
                        .build() : null
                ).stream().filter(e -> e != null).toList())
                .build();

            address.setExtension(List.of(adminCodeExt));
        }

        return address;
    }
}
