package com.quodex._miles.io;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Dashboard Statistics Response for jOOQ queries
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DashboardStatsResponse {
    private Long totalUsers;
    private Long totalProducts;
    private Long totalOrders;
    private Long totalCategories;
    private BigDecimal totalRevenue;
    private Long pendingOrders;
    private Long completedOrders;
    private Double averageOrderValue;
}
