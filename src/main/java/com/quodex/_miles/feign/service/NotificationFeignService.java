package com.quodex._miles.feign.service;

import com.quodex._miles.feign.client.NotificationServiceClient;
import com.quodex._miles.feign.dto.EmailNotificationRequest;
import com.quodex._miles.feign.dto.SmsNotificationRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Notification Service using Feign Client
 *
 * This service demonstrates how to use Feign clients for sending notifications.
 * It handles email, SMS, and push notifications through an external notification service.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationFeignService {

    private final NotificationServiceClient notificationServiceClient;

    /**
     * Send email notification
     */
    public void sendEmail(EmailNotificationRequest request) {
        log.info("Sending email to: {}", request.getTo());
        try {
            notificationServiceClient.sendEmail(request);
            log.info("Email sent successfully");
        } catch (Exception e) {
            log.error("Failed to send email", e);
            throw new RuntimeException("Failed to send email", e);
        }
    }

    /**
     * Send SMS notification
     */
    public void sendSms(SmsNotificationRequest request) {
        log.info("Sending SMS to: {}", request.getPhoneNumber());
        try {
            notificationServiceClient.sendSms(request);
            log.info("SMS sent successfully");
        } catch (Exception e) {
            log.error("Failed to send SMS", e);
            throw new RuntimeException("Failed to send SMS", e);
        }
    }

    /**
     * Send push notification
     */
    public void sendPushNotification(SmsNotificationRequest request) {
        log.info("Sending push notification to user: {}", request.getUserId());
        try {
            notificationServiceClient.sendPushNotification(request);
            log.info("Push notification sent successfully");
        } catch (Exception e) {
            log.error("Failed to send push notification", e);
            throw new RuntimeException("Failed to send push notification", e);
        }
    }

    /**
     * Send bulk email
     */
    public void sendBulkEmail(EmailNotificationRequest request) {
        log.info("Sending bulk email");
        try {
            notificationServiceClient.sendBulkEmail(request);
            log.info("Bulk email sent successfully");
        } catch (Exception e) {
            log.error("Failed to send bulk email", e);
            throw new RuntimeException("Failed to send bulk email", e);
        }
    }
}

