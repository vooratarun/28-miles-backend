package com.quodex._miles.controller;

import com.quodex._miles.feign.dto.*;
import com.quodex._miles.feign.service.NotificationFeignService;
import com.quodex._miles.feign.service.PaymentFeignService;
import com.quodex._miles.feign.service.ShippingFeignService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Feign Client Demo Controller
 *
 * This controller demonstrates how to use Feign clients to integrate with external services.
 * It provides REST endpoints that internally call external services via Feign clients.
 *
 * Endpoints:
 * - Payment Gateway integration (verify payment, refunds)
 * - Shipping Service integration (quotes, tracking)
 * - Notification Service integration (email, SMS)
 */
@RestController
@RequestMapping("/api/feign-demo")
@Tag(name = "Feign Demo", description = "Feign Client Examples and Integration")
@RequiredArgsConstructor
@Slf4j
public class FeignDemoController {

    private final PaymentFeignService paymentFeignService;
    private final ShippingFeignService shippingFeignService;
    private final NotificationFeignService notificationFeignService;

    // ==================== PAYMENT ENDPOINTS ====================

    /**
     * Verify payment with external payment gateway
     */
    @PostMapping("/payments/verify")
    @Operation(summary = "Verify payment", description = "Verify payment with external payment gateway")
    public ResponseEntity<PaymentVerificationResponse> verifyPayment(
            @RequestBody PaymentVerificationRequest request) {
        log.info("Verifying payment via Feign client");
        try {
            PaymentVerificationResponse response = paymentFeignService.verifyPayment(request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Payment verification failed", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    /**
     * Get payment details from gateway
     */
    @GetMapping("/payments/{paymentId}")
    @Operation(summary = "Get payment details", description = "Retrieve payment details from external gateway")
    public ResponseEntity<PaymentVerificationResponse> getPaymentDetails(@PathVariable String paymentId) {
        log.info("Fetching payment details for: {}", paymentId);
        try {
            PaymentVerificationResponse response = paymentFeignService.getPaymentDetails(paymentId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Failed to fetch payment details", e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    /**
     * Process refund
     */
    @PostMapping("/refunds")
    @Operation(summary = "Process refund", description = "Process refund through external payment gateway")
    public ResponseEntity<RefundResponse> processRefund(@RequestBody RefundRequest request) {
        log.info("Processing refund for payment: {}", request.getPaymentId());
        try {
            RefundResponse response = paymentFeignService.processRefund(request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Refund processing failed", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    /**
     * Check payment status
     */
    @GetMapping("/payments/{paymentId}/status")
    @Operation(summary = "Check payment status", description = "Check the current status of a payment")
    public ResponseEntity<String> checkPaymentStatus(@PathVariable String paymentId) {
        log.info("Checking payment status for: {}", paymentId);
        try {
            String status = paymentFeignService.checkPaymentStatus(paymentId);
            return ResponseEntity.ok(status);
        } catch (Exception e) {
            log.error("Failed to check payment status", e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    // ==================== SHIPPING ENDPOINTS ====================

    /**
     * Get shipping quote
     */
    @PostMapping("/shipping/quote")
    @Operation(summary = "Get shipping quote", description = "Get shipping quote from external shipping service")
    public ResponseEntity<ShippingQuoteResponse> getShippingQuote(@RequestBody ShippingQuoteRequest request) {
        log.info("Getting shipping quote via Feign client");
        try {
            ShippingQuoteResponse response = shippingFeignService.getShippingQuote(request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Failed to get shipping quote", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    /**
     * Get shipping rates
     */
    @GetMapping("/shipping/rates")
    @Operation(summary = "Get shipping rates", description = "Get available shipping rates")
    public ResponseEntity<List<ShippingQuoteResponse>> getShippingRates(
            @RequestParam String fromZip,
            @RequestParam String toZip,
            @RequestParam Double weight) {
        log.info("Fetching shipping rates");
        try {
            List<ShippingQuoteResponse> rates = shippingFeignService.getShippingRates(fromZip, toZip, weight);
            return ResponseEntity.ok(rates);
        } catch (Exception e) {
            log.error("Failed to get shipping rates", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    /**
     * Create shipping label
     */
    @PostMapping("/shipping/labels")
    @Operation(summary = "Create shipping label", description = "Create shipping label from external service")
    public ResponseEntity<ShippingQuoteResponse> createShippingLabel(@RequestBody ShippingQuoteRequest request) {
        log.info("Creating shipping label via Feign client");
        try {
            ShippingQuoteResponse response = shippingFeignService.createShippingLabel(request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Failed to create shipping label", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    /**
     * Track shipment
     */
    @GetMapping("/shipping/track/{trackingId}")
    @Operation(summary = "Track shipment", description = "Track a shipment using tracking ID")
    public ResponseEntity<ShippingQuoteResponse> trackShipment(@PathVariable String trackingId) {
        log.info("Tracking shipment: {}", trackingId);
        try {
            ShippingQuoteResponse response = shippingFeignService.trackShipment(trackingId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Failed to track shipment", e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    // ==================== NOTIFICATION ENDPOINTS ====================

    /**
     * Send email notification
     */
    @PostMapping("/notifications/email")
    @Operation(summary = "Send email", description = "Send email via external notification service")
    public ResponseEntity<String> sendEmail(@RequestBody EmailNotificationRequest request) {
        log.info("Sending email via Feign client");
        try {
            notificationFeignService.sendEmail(request);
            return ResponseEntity.ok("Email sent successfully");
        } catch (Exception e) {
            log.error("Failed to send email", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Failed to send email");
        }
    }

    /**
     * Send SMS notification
     */
    @PostMapping("/notifications/sms")
    @Operation(summary = "Send SMS", description = "Send SMS via external notification service")
    public ResponseEntity<String> sendSms(@RequestBody SmsNotificationRequest request) {
        log.info("Sending SMS via Feign client");
        try {
            notificationFeignService.sendSms(request);
            return ResponseEntity.ok("SMS sent successfully");
        } catch (Exception e) {
            log.error("Failed to send SMS", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Failed to send SMS");
        }
    }

    /**
     * Send push notification
     */
    @PostMapping("/notifications/push")
    @Operation(summary = "Send push notification", description = "Send push notification via external service")
    public ResponseEntity<String> sendPushNotification(@RequestBody SmsNotificationRequest request) {
        log.info("Sending push notification via Feign client");
        try {
            notificationFeignService.sendPushNotification(request);
            return ResponseEntity.ok("Push notification sent successfully");
        } catch (Exception e) {
            log.error("Failed to send push notification", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Failed to send push notification");
        }
    }

    /**
     * Send bulk email
     */
    @PostMapping("/notifications/email/bulk")
    @Operation(summary = "Send bulk email", description = "Send bulk email via external notification service")
    public ResponseEntity<String> sendBulkEmail(@RequestBody EmailNotificationRequest request) {
        log.info("Sending bulk email via Feign client");
        try {
            notificationFeignService.sendBulkEmail(request);
            return ResponseEntity.ok("Bulk email sent successfully");
        } catch (Exception e) {
            log.error("Failed to send bulk email", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Failed to send bulk email");
        }
    }
}

