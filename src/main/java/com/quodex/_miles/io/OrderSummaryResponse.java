package com.quodex._miles.io;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Order Summary Response for jOOQ queries
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderSummaryResponse {
    private String orderId;
    private String userId;
    private String userName;
    private String userEmail;
    private BigDecimal totalAmount;
    private String status;
    private Long itemCount;
}
