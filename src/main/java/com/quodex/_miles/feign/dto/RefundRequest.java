package com.quodex._miles.feign.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Refund Request DTO
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RefundRequest {
    private String paymentId;
    private BigDecimal amount; // partial refund if specified, full if null
    private String reason;
    private String orderId;
}

