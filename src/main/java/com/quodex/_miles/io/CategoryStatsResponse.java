package com.quodex._miles.io;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Category Statistics Response for jOOQ queries
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategoryStatsResponse {
    private String categoryId;
    private String categoryName;
    private Long productCount;
    private Long totalStock;
    private BigDecimal averagePrice;
    private BigDecimal minPrice;
    private BigDecimal maxPrice;
}
