package com.quodex._miles.controller;

import com.quodex._miles.io.*;
import com.quodex._miles.service.jooq.JooqDemoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

/**
 * jOOQ Demo Controller
 *
 * This controller demonstrates various jOOQ query patterns through REST API endpoints.
 * All endpoints use type-safe jOOQ queries instead of JPA/Hibernate.
 *
 * Base URL: /api/v1.0/jooq-demo
 *
 * Endpoints showcase:
 * - Aggregation queries (statistics)
 * - Complex JOINs (analytics)
 * - Dynamic WHERE conditions (search)
 * - Multiple table relationships
 * - Performance-optimized queries
 */
@RestController
@RequestMapping("/jooq-demo")
@RequiredArgsConstructor
@Slf4j
public class JooqDemoController {

    private final JooqDemoService jooqDemoService;

    /**
     * GET /api/v1.0/jooq-demo/stats/users
     *
     * Get user statistics including counts by role and verification status.
     * Demonstrates aggregation queries with GROUP BY.
     *
     * @return User statistics
     */
    @GetMapping("/stats/users")
    public ResponseEntity<UserStatsResponse> getUserStatistics() {
        log.info("API: Getting user statistics");
        UserStatsResponse stats = jooqDemoService.getUserStatistics();
        return ResponseEntity.ok(stats);
    }

    /**
     * GET /api/v1.0/jooq-demo/analytics/products
     *
     * Get product analytics with reviews, ratings, and sales data.
     * Demonstrates complex JOINs across multiple tables.
     *
     * @return List of product analytics
     */
    @GetMapping("/analytics/products")
    public ResponseEntity<List<ProductAnalyticsResponse>> getProductAnalytics() {
        log.info("API: Getting product analytics");
        List<ProductAnalyticsResponse> analytics = jooqDemoService.getProductAnalytics();
        return ResponseEntity.ok(analytics);
    }

    /**
     * GET /api/v1.0/jooq-demo/stats/categories
     *
     * Get category statistics including product counts and price ranges.
     * Demonstrates GROUP BY with multiple aggregation functions.
     *
     * @return List of category statistics
     */
    @GetMapping("/stats/categories")
    public ResponseEntity<List<CategoryStatsResponse>> getCategoryStatistics() {
        log.info("API: Getting category statistics");
        List<CategoryStatsResponse> stats = jooqDemoService.getCategoryStatistics();
        return ResponseEntity.ok(stats);
    }

    /**
     * GET /api/v1.0/jooq-demo/orders/recent?limit=10
     *
     * Get recent orders with user details and item counts.
     * Demonstrates multiple JOINs and pagination.
     *
     * @param limit Maximum number of orders to return (default: 10)
     * @return List of recent order summaries
     */
    @GetMapping("/orders/recent")
    public ResponseEntity<List<OrderSummaryResponse>> getRecentOrders(
            @RequestParam(defaultValue = "10") int limit) {
        log.info("API: Getting recent {} orders", limit);
        List<OrderSummaryResponse> orders = jooqDemoService.getRecentOrders(limit);
        return ResponseEntity.ok(orders);
    }

    /**
     * GET /api/v1.0/jooq-demo/dashboard/stats
     *
     * Get comprehensive dashboard statistics in a single optimized query.
     * Demonstrates multiple parallel aggregations.
     *
     * @return Dashboard statistics
     */
    @GetMapping("/dashboard/stats")
    public ResponseEntity<DashboardStatsResponse> getDashboardStatistics() {
        log.info("API: Getting dashboard statistics");
        DashboardStatsResponse stats = jooqDemoService.getDashboardStatistics();
        return ResponseEntity.ok(stats);
    }

    /**
     * GET /api/v1.0/jooq-demo/users/search?name=john&email=gmail&role=USER&verified=true
     *
     * Search users with flexible criteria.
     * Demonstrates dynamic WHERE conditions based on request parameters.
     *
     * @param name Filter by name (partial match, case-insensitive)
     * @param email Filter by email (partial match, case-insensitive)
     * @param role Filter by exact role (ADMIN, SELLER, USER)
     * @param verified Filter by verification status
     * @return List of matching users
     */
    @GetMapping("/users/search")
    public ResponseEntity<List<UserResponse>> searchUsers(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String role,
            @RequestParam(required = false) Boolean verified) {
        log.info("API: Searching users - name: {}, email: {}, role: {}, verified: {}",
                name, email, role, verified);
        List<UserResponse> users = jooqDemoService.searchUsers(name, email, role, verified);
        return ResponseEntity.ok(users);
    }

    /**
     * GET /api/v1.0/jooq-demo/products/price-range?categoryId=1&minPrice=50&maxPrice=200
     *
     * Get products within a price range, optionally filtered by category.
     * Demonstrates BETWEEN queries and conditional filtering.
     *
     * @param categoryId Optional category filter
     * @param minPrice Minimum price filter
     * @param maxPrice Maximum price filter
     * @return List of products in price range
     */
    @GetMapping("/products/price-range")
    public ResponseEntity<List<ProductResponse>> getProductsByPriceRange(
            @RequestParam(required = false) String categoryId,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice) {
        log.info("API: Getting products by price range - category: {}, min: {}, max: {}",
                categoryId, minPrice, maxPrice);
        List<ProductResponse> products = jooqDemoService.getProductsByPriceRange(categoryId, minPrice, maxPrice);
        return ResponseEntity.ok(products);
    }

    /**
     * GET /api/v1.0/jooq-demo/products/top-selling?limit=5
     *
     * Get top-selling products by total quantity sold.
     * Demonstrates complex JOINs with ORDER_ITEM and ORDER tables.
     *
     * @param limit Maximum number of products to return (default: 5)
     * @return List of top-selling products
     */
    @GetMapping("/products/top-selling")
    public ResponseEntity<List<ProductAnalyticsResponse>> getTopSellingProducts(
            @RequestParam(defaultValue = "5") int limit) {
        log.info("API: Getting top {} selling products", limit);
        List<ProductAnalyticsResponse> products = jooqDemoService.getTopSellingProducts(limit);
        return ResponseEntity.ok(products);
    }

    /**
     * GET /api/v1.0/jooq-demo/health
     *
     * Health check endpoint to verify jOOQ integration is working.
     * Performs a simple count query to test database connectivity.
     *
     * @return Health status
     */
    @GetMapping("/health")
    public ResponseEntity<String> healthCheck() {
        try {
            // Simple query to test jOOQ connectivity
            UserStatsResponse stats = jooqDemoService.getUserStatistics();
            return ResponseEntity.ok("jOOQ integration is healthy! Total users: " + stats.getTotalUsers());
        } catch (Exception e) {
            log.error("jOOQ health check failed", e);
            return ResponseEntity.internalServerError()
                .body("jOOQ integration failed: " + e.getMessage());
        }
    }
}
