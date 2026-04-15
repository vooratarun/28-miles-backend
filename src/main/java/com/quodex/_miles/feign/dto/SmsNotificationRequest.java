package com.quodex._miles.feign.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * SMS Notification Request DTO
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SmsNotificationRequest {
    private String phoneNumber;
    private String message;
    private String template; // template name
    private String userId;
    private String type; // sms, push, whatsapp
}

