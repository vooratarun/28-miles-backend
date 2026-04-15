package com.quodex._miles.feign.client;

import com.quodex._miles.feign.dto.EmailNotificationRequest;
import com.quodex._miles.feign.dto.SmsNotificationRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * Notification Service Feign Client
 *
 * This client demonstrates integration with external notification services.
 * Example: Email, SMS, Push notifications via third-party providers.
 *
 * Example URL Configuration in application.properties:
 * feign.client.config.notification-service.url=https://api.notification-service.com
 * feign.client.config.notification-service.connectTimeout=3000
 * feign.client.config.notification-service.readTimeout=5000
 */
@FeignClient(
    name = "notification-service",
    url = "${notification.service.url:http://localhost:8080}",
    configuration = NotificationFeignConfig.class
)
public interface NotificationServiceClient {

    /**
     * Send email notification
     */
    @PostMapping("/api/notifications/email")
    void sendEmail(@RequestBody EmailNotificationRequest request);

    /**
     * Send SMS notification
     */
    @PostMapping("/api/notifications/sms")
    void sendSms(@RequestBody SmsNotificationRequest request);

    /**
     * Send push notification
     */
    @PostMapping("/api/notifications/push")
    void sendPushNotification(@RequestBody SmsNotificationRequest request);

    /**
     * Send bulk email
     */
    @PostMapping("/api/notifications/email/bulk")
    void sendBulkEmail(@RequestBody EmailNotificationRequest request);
}

