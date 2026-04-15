package com.quodex._miles.feign.client;

import feign.Logger;
import feign.codec.ErrorDecoder;
import okhttp3.OkHttpClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Notification Service Feign Configuration
 *
 * Provides custom configuration for the Notification Service Feign Client
 */
@Configuration
public class NotificationFeignConfig {

    @Bean
    public OkHttpClient client() {
        return new OkHttpClient();
    }

    @Bean
    public Logger.Level feignLoggerLevel() {
        return Logger.Level.BASIC; // Log only request/response lines
    }

    @Bean
    public ErrorDecoder errorDecoder() {
        return new NotificationErrorDecoder();
    }
}

