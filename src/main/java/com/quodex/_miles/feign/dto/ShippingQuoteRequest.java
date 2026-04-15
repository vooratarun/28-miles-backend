package com.quodex._miles.feign.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Shipping Quote Request DTO
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ShippingQuoteRequest {
    private String fromZip;
    private String toZip;
    private Double weight;
    private String dimensions; // length x width x height
    private String serviceType; // standard, express, overnight
    private String packageType; // box, envelope, etc
}

