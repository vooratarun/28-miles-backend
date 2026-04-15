package com.quodex._miles.feign.client;

import feign.Response;
import feign.codec.ErrorDecoder;
import lombok.extern.slf4j.Slf4j;

/**
 * Custom Error Decoder for Payment Gateway
 *
 * Handles different HTTP error codes for payment operations
 */
@Slf4j
public class PaymentErrorDecoder implements ErrorDecoder {

    @Override
    public Exception decode(String methodKey, Response response) {
        log.error("Payment Gateway Error: {} - {}", response.status(), response.reason());

        return switch (response.status()) {
            case 400 -> new IllegalArgumentException("Invalid payment request");
            case 401 -> new SecurityException("Payment gateway authentication failed");
            case 402 -> new RuntimeException("Payment declined");
            case 404 -> new RuntimeException("Payment record not found");
            case 408 -> new RuntimeException("Payment timeout");
            case 429 -> new RuntimeException("Payment service rate limited");
            case 500, 502, 503 -> new RuntimeException("Payment service is temporarily unavailable");
            default -> new RuntimeException("Payment service error: " + response.reason());
        };
    }
}

