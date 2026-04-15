package com.quodex._miles.feign.client;

import com.quodex._miles.feign.dto.PaymentVerificationRequest;
import com.quodex._miles.feign.dto.PaymentVerificationResponse;
import com.quodex._miles.feign.dto.RefundRequest;
import com.quodex._miles.feign.dto.RefundResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

/**
 * Payment Gateway Feign Client
 *
 * This client demonstrates integration with external payment gateways.
 * Example: Could integrate with Razorpay, Stripe, PayPal, etc.
 *
 * Example URL Configuration in application.properties:
 * feign.client.config.payment-gateway.url=https://api.payment-gateway.com
 * feign.client.config.payment-gateway.connectTimeout=5000
 * feign.client.config.payment-gateway.readTimeout=15000
 */
@FeignClient(
    name = "payment-gateway",
    url = "${payment.gateway.url:http://localhost:8080}",
    configuration = PaymentFeignConfig.class
)
public interface PaymentGatewayClient {

    /**
     * Verify payment status
     */
    @PostMapping("/api/payments/verify")
    PaymentVerificationResponse verifyPayment(@RequestBody PaymentVerificationRequest request);

    /**
     * Get payment details
     */
    @GetMapping("/api/payments/{paymentId}")
    PaymentVerificationResponse getPaymentDetails(@PathVariable String paymentId);

    /**
     * Initiate refund
     */
    @PostMapping("/api/refunds")
    RefundResponse initiateRefund(@RequestBody RefundRequest request);

    /**
     * Get refund status
     */
    @GetMapping("/api/refunds/{refundId}")
    RefundResponse getRefundStatus(@PathVariable String refundId);

    /**
     * Check payment status (polling alternative)
     */
    @GetMapping("/api/payments/{paymentId}/status")
    String getPaymentStatus(@PathVariable String paymentId);
}

