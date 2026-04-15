package com.quodex._miles.feign.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Payment Verification Response DTO
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PaymentVerificationResponse {
    private String paymentId;
    private String status; // captured, pending, failed, refunded
    private BigDecimal amount;
    private String currency;
    private String method; // card, netbanking, upi, wallet
    private LocalDateTime createdAt;
    private String orderId;
    private Boolean authenticated;
    private String failureReason;
}

