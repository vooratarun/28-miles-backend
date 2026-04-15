package com.quodex._miles.feign.interceptor;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Custom Request Interceptor for Feign Clients
 *
 * This interceptor can be used to add common headers, authentication, or other
 * request modifications to all Feign client requests.
 */
@Component
@Slf4j
public class FeignRequestInterceptor implements RequestInterceptor {

    @Override
    public void apply(RequestTemplate template) {
        log.debug("Intercepting request to: {}", template.url());

        // Add custom headers
        template.header("X-Request-ID", generateRequestId());
        template.header("X-Service-Name", "28miles");
        template.header("Content-Type", "application/json");
        template.header("Accept", "application/json");

        // Add timestamp
        template.header("X-Request-Time", String.valueOf(System.currentTimeMillis()));

        // Add Authorization header if needed (can be conditionally added)
        // template.header("Authorization", "Bearer " + getAuthToken());
    }

    /**
     * Generate unique request ID for tracing
     */
    private String generateRequestId() {
        return "REQ-" + System.currentTimeMillis() + "-" + System.nanoTime();
    }

    /**
     * Get authorization token (implement based on your auth mechanism)
     */
    private String getAuthToken() {
        // Implement token retrieval logic
        return "token"; // Placeholder
    }
}

