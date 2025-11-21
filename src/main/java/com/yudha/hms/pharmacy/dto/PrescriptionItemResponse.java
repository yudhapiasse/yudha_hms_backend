package com.yudha.hms.pharmacy.dto;

import com.yudha.hms.pharmacy.constant.DosageFrequency;
import com.yudha.hms.pharmacy.constant.RouteOfAdministration;
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
public class PrescriptionItemResponse {
    private UUID id;
    private Integer lineNumber;
    private UUID drugId;
    private String drugCode;
    private String drugName;
    private String strength;
    private String dosageForm;
    private BigDecimal doseQuantity;
    private String doseUnit;
    private DosageFrequency frequency;
    private String customFrequency;
    private RouteOfAdministration route;
    private Integer durationDays;
    private BigDecimal quantityToDispense;
    private BigDecimal quantityDispensed;
    private BigDecimal unitPrice;
    private BigDecimal totalPrice;
    private String instructions;
    private String specialInstructions;
    private Boolean isPrn;
    private String prnIndication;
    private Boolean substitutionAllowed;
    private UUID substitutedDrugId;
    private String substitutedDrugName;
    private String substitutionReason;
    private Boolean isControlled;
    private Boolean isHighAlert;
    private String interactionWarnings;
    private Boolean labelPrinted;
}
