package com.quodex._miles.feign.client;

import feign.Response;
import feign.codec.ErrorDecoder;
import lombok.extern.slf4j.Slf4j;

/**
 * Custom Error Decoder for Notification Service
 *
 * Handles errors during notification operations
 */
@Slf4j
public class NotificationErrorDecoder implements ErrorDecoder {

    @Override
    public Exception decode(String methodKey, Response response) {
        log.error("Notification Service Error: {} - {}", response.status(), response.reason());

        return switch (response.status()) {
            case 400 -> new IllegalArgumentException("Invalid notification request");
            case 401 -> new SecurityException("Notification service authentication failed");
            case 404 -> new RuntimeException("Notification recipient not found");
            case 429 -> new RuntimeException("Notification service rate limited");
            case 500, 502, 503 -> new RuntimeException("Notification service is temporarily unavailable");
            default -> new RuntimeException("Notification service error: " + response.reason());
        };
    }
}

