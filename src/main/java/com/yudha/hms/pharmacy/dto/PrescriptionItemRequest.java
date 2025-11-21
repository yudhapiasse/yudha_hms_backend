package com.yudha.hms.pharmacy.dto;

import com.yudha.hms.pharmacy.constant.DosageFrequency;
import com.yudha.hms.pharmacy.constant.RouteOfAdministration;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PrescriptionItemRequest {
    
    @NotNull(message = "Drug ID is required")
    private UUID drugId;

    @NotNull(message = "Dose quantity is required")
    @DecimalMin(value = "0.01", message = "Dose quantity must be greater than 0")
    private BigDecimal doseQuantity;

    @NotBlank(message = "Dose unit is required")
    @Size(max = 50)
    private String doseUnit;

    @NotNull(message = "Frequency is required")
    private DosageFrequency frequency;

    @Size(max = 200)
    private String customFrequency;

    @NotNull(message = "Route of administration is required")
    private RouteOfAdministration route;

    @Min(value = 1, message = "Duration must be at least 1 day")
    private Integer durationDays;

    @NotNull(message = "Quantity to dispense is required")
    @DecimalMin(value = "0.01", message = "Quantity must be greater than 0")
    private BigDecimal quantityToDispense;

    private String instructions;

    private String specialInstructions;

    @Builder.Default
    private Boolean isPrn = false;

    @Size(max = 200)
    private String prnIndication;

    @Builder.Default
    private Boolean substitutionAllowed = true;
}
