package com.quodex._miles.feign.service;

import com.quodex._miles.feign.client.PaymentGatewayClient;
import com.quodex._miles.feign.dto.PaymentVerificationRequest;
import com.quodex._miles.feign.dto.PaymentVerificationResponse;
import com.quodex._miles.feign.dto.RefundRequest;
import com.quodex._miles.feign.dto.RefundResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Payment Service using Feign Client
 *
 * This service demonstrates how to use Feign clients in your business logic.
 * It handles payment verification and refunds through the external payment gateway.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentFeignService {

    private final PaymentGatewayClient paymentGatewayClient;

    /**
     * Verify payment with the payment gateway
     */
    public PaymentVerificationResponse verifyPayment(PaymentVerificationRequest request) {
        log.info("Verifying payment for order: {}", request.getOrderId());
        try {
            return paymentGatewayClient.verifyPayment(request);
        } catch (Exception e) {
            log.error("Failed to verify payment", e);
            throw new RuntimeException("Payment verification failed", e);
        }
    }

    /**
     * Get payment details from gateway
     */
    public PaymentVerificationResponse getPaymentDetails(String paymentId) {
        log.info("Fetching payment details for: {}", paymentId);
        try {
            return paymentGatewayClient.getPaymentDetails(paymentId);
        } catch (Exception e) {
            log.error("Failed to fetch payment details", e);
            throw new RuntimeException("Failed to fetch payment details", e);
        }
    }

    /**
     * Process refund
     */
    public RefundResponse processRefund(RefundRequest request) {
        log.info("Processing refund for payment: {}", request.getPaymentId());
        try {
            return paymentGatewayClient.initiateRefund(request);
        } catch (Exception e) {
            log.error("Failed to process refund", e);
            throw new RuntimeException("Refund processing failed", e);
        }
    }

    /**
     * Check payment status
     */
    public String checkPaymentStatus(String paymentId) {
        log.info("Checking payment status for: {}", paymentId);
        try {
            return paymentGatewayClient.getPaymentStatus(paymentId);
        } catch (Exception e) {
            log.error("Failed to check payment status", e);
            throw new RuntimeException("Failed to check payment status", e);
        }
    }
}

