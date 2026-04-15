package com.quodex._miles.io;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * User Statistics Response for jOOQ queries
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserStatsResponse {
    private Long totalUsers;
    private Long verifiedUsers;
    private Integer adminUsers;
    private Integer sellerUsers;
    private Integer customerUsers;
}
