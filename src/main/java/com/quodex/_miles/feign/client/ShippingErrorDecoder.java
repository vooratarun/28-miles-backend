package com.quodex._miles.feign.client;

import feign.Response;
import feign.codec.ErrorDecoder;
import lombok.extern.slf4j.Slf4j;

/**
 * Custom Error Decoder for Shipping Service
 *
 * Handles different HTTP error codes and converts them to application exceptions
 */
@Slf4j
public class ShippingErrorDecoder implements ErrorDecoder {

    @Override
    public Exception decode(String methodKey, Response response) {
        log.error("Shipping Service Error: {} - {}", response.status(), response.reason());

        return switch (response.status()) {
            case 400 -> new IllegalArgumentException("Bad request to shipping service");
            case 401 -> new SecurityException("Unauthorized access to shipping service");
            case 404 -> new RuntimeException("Shipping service resource not found");
            case 500, 502, 503 -> new RuntimeException("Shipping service is temporarily unavailable");
            default -> new RuntimeException("Shipping service error: " + response.reason());
        };
    }
}

