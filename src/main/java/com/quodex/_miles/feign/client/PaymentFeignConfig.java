package com.quodex._miles.feign.client;

import feign.Logger;
import feign.codec.ErrorDecoder;
import feign.okhttp.OkHttpClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Payment Gateway Feign Configuration
 *
 * Provides custom configuration for the Payment Gateway Feign Client:
 * - HTTP client setup
 * - Logging level
 * - Error handling for payment operations
 */
@Configuration
public class PaymentFeignConfig {

    @Bean
    public OkHttpClient client() {
        return new OkHttpClient();
    }

    @Bean
    public Logger.Level feignLoggerLevel() {
        return Logger.Level.FULL; // Log request/response body
    }

    @Bean
    public ErrorDecoder errorDecoder() {
        return new PaymentErrorDecoder();
    }
}

