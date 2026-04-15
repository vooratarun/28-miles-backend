package com.quodex._miles.feign.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Email Notification Request DTO
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EmailNotificationRequest {
    private String to;
    private String cc;
    private String bcc;
    private String subject;
    private String template; // template name
    private String body;
    private String htmlContent;
    private Boolean isHtml;
}

