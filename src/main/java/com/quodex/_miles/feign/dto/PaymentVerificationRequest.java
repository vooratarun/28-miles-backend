package com.quodex._miles.feign.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Payment Verification Request DTO
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PaymentVerificationRequest {
    private String paymentId;
    private String signature;
    private String orderId;
    private String amount;
    private String currency;
}

