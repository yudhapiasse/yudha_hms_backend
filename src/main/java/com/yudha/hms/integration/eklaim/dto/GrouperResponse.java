package com.yudha.hms.integration.eklaim.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * Response DTO for grouper execution.
 *
 * Contains grouping results including:
 * - DRG/CBG code
 * - Tariff calculation
 * - Top-up details
 * - Special CMG/drug/prosthesis
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-20
 */
@Data
public class GrouperResponse {

    /**
     * DRG/CBG code assigned
     */
    @JsonProperty("code")
    private String code;

    /**
     * DRG/CBG description
     */
    @JsonProperty("description")
    private String description;

    /**
     * Base tariff
     */
    @JsonProperty("base_tariff")
    private BigDecimal baseTariff;

    /**
     * Top-up for COVID-19 cases
     */
    @JsonProperty("top_up_covid")
    private BigDecimal topUpCovid;

    /**
     * Top-up for chronic diseases
     */
    @JsonProperty("top_up_chronic")
    private BigDecimal topUpChronic;

    /**
     * Upgrade class fee (if patient upgraded room class)
     */
    @JsonProperty("upgrade_class")
    private BigDecimal upgradeClass;

    /**
     * Special CMG (Chronic Management Group) tariff
     */
    @JsonProperty("special_cmg")
    private BigDecimal specialCmg;

    /**
     * Special prosthesis tariff
     */
    @JsonProperty("special_prosthesis")
    private BigDecimal specialProsthesis;

    /**
     * Special drug tariff (high-cost medications)
     */
    @JsonProperty("special_drug")
    private BigDecimal specialDrug;

    /**
     * Total tariff (base + all top-ups and specials)
     */
    @JsonProperty("total_tariff")
    private BigDecimal totalTariff;

    /**
     * Grouping messages/warnings
     */
    @JsonProperty("messages")
    private List<GroupingMessage> messages;

    /**
     * Is this claim groupable?
     */
    @JsonProperty("is_groupable")
    private Boolean isGroupable;

    /**
     * Ungroupable reason code (36.xxxx series if ungroupable)
     */
    @JsonProperty("ungroupable_code")
    private String ungroupableCode;

    @Data
    public static class GroupingMessage {
        @JsonProperty("code")
        private String code;

        @JsonProperty("message")
        private String message;

        @JsonProperty("severity")
        private String severity; // INFO, WARNING, ERROR
    }
}
