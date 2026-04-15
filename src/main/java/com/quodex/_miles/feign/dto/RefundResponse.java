package com.quodex._miles.feign.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Refund Response DTO
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RefundResponse {
    private String refundId;
    private String paymentId;
    private BigDecimal amount;
    private String status; // pending, processed, failed
    private LocalDateTime createdAt;
    private LocalDateTime processedAt;
    private String reason;
    private String notes;
}

