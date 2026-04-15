package com.quodex._miles.io;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Product Analytics Response for jOOQ queries
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductAnalyticsResponse {
    private String productId;
    private String productName;
    private BigDecimal price;
    private Long reviewCount;
    private Double averageRating;
    private Long orderCount;
    private BigDecimal totalRevenue;
}
