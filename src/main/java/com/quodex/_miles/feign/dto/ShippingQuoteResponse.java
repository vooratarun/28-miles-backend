package com.quodex._miles.feign.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Shipping Quote Response DTO
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ShippingQuoteResponse {
    private String quoteId;
    private BigDecimal cost;
    private String currency;
    private String serviceType;
    private Integer estimatedDays;
    private LocalDateTime estimatedDelivery;
    private String trackingNumber;
    private String status; // quoted, created, shipped, delivered
    private String carrier;
}

