package com.quodex._miles.service.jooq;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jooq.DSLContext;
import org.springframework.stereotype.Service;

/**
 * Product jOOQ Service Example
 *
 * This service demonstrates how to use jOOQ for querying products with
 * complex conditions, aggregations, and joins that would be cumbersome
 * with Spring Data JPA.
 *
 * Usage in controller:
 * @Autowired
 * private ProductJooqService productJooqService;
 *
 * var topProducts = productJooqService.getTopProductsByRating(10);
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ProductJooqService {

    private final DSLContext dsl;

    /**
     * Get products with review statistics
     *
     * This query demonstrates:
     * - LEFT JOIN for optional relationships
     * - Aggregation functions (COUNT, AVG)
     * - GROUP BY with HAVING
     * - ORDER BY on aggregated columns
     */
    public void getProductsWithReviewStats() {
        log.info("Fetching products with review statistics");

        // After code generation:
        // var result = dsl.select(
        //     PRODUCT.ID,
        //     PRODUCT.NAME,
        //     PRODUCT.PRICE,
        //     PRODUCT.CATEGORY_ID,
        //     count(REVIEW.ID).as("reviewCount"),
        //     avg(REVIEW.RATING).as("avgRating"),
        //     max(REVIEW.RATING).as("maxRating"),
        //     min(REVIEW.RATING).as("minRating")
        // )
        // .from(PRODUCT)
        // .leftJoin(REVIEW).on(REVIEW.PRODUCT_ID.eq(PRODUCT.ID))
        // .groupBy(PRODUCT.ID, PRODUCT.NAME, PRODUCT.PRICE, PRODUCT.CATEGORY_ID)
        // .having(count(REVIEW.ID).greaterThan(5))  // Only products with 5+ reviews
        // .orderBy(avg(REVIEW.RATING).desc())
        // .fetch();
    }

    /**
     * Get products by category with filtering
     *
     * Demonstrates:
     * - Multiple WHERE conditions
     * - LIKE pattern matching
     * - BETWEEN for price range
     */
    public void getProductsByCategory(Long categoryId, Double minPrice, Double maxPrice) {
        log.info("Fetching products for category: {}", categoryId);

        // After code generation:
        // var products = dsl.selectFrom(PRODUCT)
        //     .where(PRODUCT.CATEGORY_ID.eq(categoryId))
        //     .and(PRODUCT.PRICE.between(minPrice, maxPrice))
        //     .and(PRODUCT.ACTIVE.eq(true))  // Assuming active column
        //     .orderBy(PRODUCT.NAME)
        //     .fetch();
    }

    /**
     * Get bestselling products
     *
     * Demonstrates:
     * - Complex JOIN with multiple tables
     * - Aggregation with COUNT
     * - Limiting results
     */
    public void getBestsellingProducts(int limit) {
        log.info("Fetching {} bestselling products", limit);

        // After code generation:
        // var result = dsl.select(
        //     PRODUCT.ID,
        //     PRODUCT.NAME,
        //     count(ORDER_ITEM.ID).as("orderCount"),
        //     sum(ORDER_ITEM.QUANTITY).as("totalQuantity"),
        //     sum(ORDER_ITEM.QUANTITY.multiply(ORDER_ITEM.PRICE)).as("totalRevenue")
        // )
        // .from(PRODUCT)
        // .join(ORDER_ITEM).on(ORDER_ITEM.PRODUCT_ID.eq(PRODUCT.ID))
        // .join(ORDER).on(ORDER.ID.eq(ORDER_ITEM.ORDER_ID))
        // .where(ORDER.STATUS.eq("COMPLETED"))  // Only completed orders
        // .groupBy(PRODUCT.ID, PRODUCT.NAME)
        // .orderBy(sum(ORDER_ITEM.QUANTITY).desc())
        // .limit(limit)
        // .fetch();
    }

    /**
     * Search products
     *
     * Demonstrates:
     * - Case-insensitive search
     * - OR conditions
     * - Dynamic SQL building
     */
    public void searchProducts(String keyword) {
        log.info("Searching products with keyword: {}", keyword);

        // After code generation:
        // var keyword_lower = "%" + keyword.toLowerCase() + "%";
        // var products = dsl.selectFrom(PRODUCT)
        //     .where(PRODUCT.NAME.likeIgnoreCase(keyword_lower))
        //     .or(PRODUCT.DESCRIPTION.likeIgnoreCase(keyword_lower))
        //     .or(PRODUCT.SKU.likeIgnoreCase(keyword_lower))
        //     .fetch();
    }

    /**
     * Get products in stock near minimum quantity
     *
     * Demonstrates:
     * - Complex conditions with AND/OR
     * - Comparison operators
     * - ORDER BY for sorting
     */
    public void getLowStockProducts(int threshold) {
        log.info("Fetching low stock products (threshold: {})", threshold);

        // After code generation:
        // var products = dsl.selectFrom(PRODUCT)
        //     .where(PRODUCT.STOCK_QUANTITY.lessOrEqual(threshold))
        //     .and(PRODUCT.STOCK_QUANTITY.greaterThan(0))
        //     .orderBy(PRODUCT.STOCK_QUANTITY)
        //     .fetch();
    }

    /**
     * Get product details with all related data
     *
     * Demonstrates:
     * - Multiple JOINs
     * - Selecting from multiple tables
     * - Handling result mapping
     */
    public void getProductDetails(Long productId) {
        log.info("Fetching details for product: {}", productId);

        // After code generation:
        // var result = dsl.select(
        //     PRODUCT.asterisk(),
        //     CATEGORY.NAME.as("categoryName"),
        //     count(REVIEW.ID).as("reviewCount"),
        //     avg(REVIEW.RATING).as("avgRating")
        // )
        // .from(PRODUCT)
        // .join(CATEGORY).on(PRODUCT.CATEGORY_ID.eq(CATEGORY.ID))
        // .leftJoin(REVIEW).on(REVIEW.PRODUCT_ID.eq(PRODUCT.ID))
        // .where(PRODUCT.ID.eq(productId))
        // .groupBy(PRODUCT.ID, CATEGORY.NAME)
        // .fetchOne();
    }

    /**
     * Batch update product prices
     *
     * Demonstrates:
     * - UPDATE with WHERE conditions
     * - Applying same change to multiple records
     */
    public void applyDiscountToCategory(Long categoryId, Double discountPercentage) {
        log.info("Applying {}% discount to category: {}", discountPercentage, categoryId);

        // After code generation:
        // dsl.update(PRODUCT)
        //     .set(PRODUCT.PRICE, PRODUCT.PRICE.multiply(1 - discountPercentage / 100))
        //     .where(PRODUCT.CATEGORY_ID.eq(categoryId))
        //     .execute();
    }

    /**
     * Insert product with automatic ID generation
     *
     * Demonstrates:
     * - INSERT with multiple columns
     * - Returning generated ID
     */
    public void createProduct(String name, String description, Double price, Long categoryId) {
        log.info("Creating product: {}", name);

        // After code generation:
        // var record = dsl.insertInto(PRODUCT)
        //     .set(PRODUCT.NAME, name)
        //     .set(PRODUCT.DESCRIPTION, description)
        //     .set(PRODUCT.PRICE, price)
        //     .set(PRODUCT.CATEGORY_ID, categoryId)
        //     .set(PRODUCT.ACTIVE, true)
        //     .returningResult(PRODUCT.ID)
        //     .fetchOne();
        //
        // return record.get(PRODUCT.ID);
    }

    /**
     * Get products grouped by category with count
     *
     * Demonstrates:
     * - GROUP BY with HAVING
     * - Aggregation in SELECT
     */
    public void getProductCountByCategory() {
        log.info("Fetching product count by category");

        // After code generation:
        // var result = dsl.select(
        //     CATEGORY.ID,
        //     CATEGORY.NAME,
        //     count().as("productCount")
        // )
        // .from(PRODUCT)
        // .join(CATEGORY).on(PRODUCT.CATEGORY_ID.eq(CATEGORY.ID))
        // .groupBy(CATEGORY.ID, CATEGORY.NAME)
        // .orderBy(count().desc())
        // .fetch();
    }
}

