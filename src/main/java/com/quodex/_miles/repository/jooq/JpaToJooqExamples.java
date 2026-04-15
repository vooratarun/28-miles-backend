package com.quodex._miles.repository.jooq;

/**
 * JPA vs jOOQ Query Examples - Quick Reference
 *
 * This file demonstrates how to convert common JPA queries to jOOQ equivalents.
 * All examples use static imports:
 * import static com.quodex._miles.jooq.generated.Tables.*;
 */

public class JpaToJooqExamples {

    // ============================================================================
    // 1. SIMPLE SELECT BY ID
    // ============================================================================

    // JPA:
    // User user = userRepository.findById(1L).orElse(null);

    // jOOQ:
    // var user = dsl.selectFrom(USER)
    //     .where(USER.ID.eq(1L))
    //     .fetchOne();


    // ============================================================================
    // 2. FIND BY PROPERTY
    // ============================================================================

    // JPA:
    // Optional<User> user = userRepository.findByEmail("user@example.com");

    // jOOQ:
    // var user = dsl.selectFrom(USER)
    //     .where(USER.EMAIL.eq("user@example.com"))
    //     .fetchOptional();


    // ============================================================================
    // 3. FIND ALL
    // ============================================================================

    // JPA:
    // List<User> users = userRepository.findAll();

    // jOOQ:
    // var users = dsl.selectFrom(USER)
    //     .fetch();


    // ============================================================================
    // 4. FIND ALL WITH PAGINATION
    // ============================================================================

    // JPA:
    // Page<User> users = userRepository.findAll(PageRequest.of(0, 20));

    // jOOQ:
    // var users = dsl.selectFrom(USER)
    //     .limit(20)
    //     .offset(0)
    //     .fetch();


    // ============================================================================
    // 5. FIND WITH CUSTOM WHERE CLAUSE
    // ============================================================================

    // JPA:
    // @Query("SELECT u FROM User u WHERE u.verified = true AND u.role = 'ADMIN'")
    // List<User> findVerifiedAdmins();

    // jOOQ:
    // var admins = dsl.selectFrom(USER)
    //     .where(USER.VERIFIED.eq(true))
    //     .and(USER.ROLE.eq("ADMIN"))
    //     .fetch();


    // ============================================================================
    // 6. FIND WITH LIKE/CONTAINS
    // ============================================================================

    // JPA:
    // List<User> users = userRepository.findByNameContainingIgnoreCase("john");

    // jOOQ:
    // var users = dsl.selectFrom(USER)
    //     .where(USER.NAME.likeIgnoreCase("%john%"))
    //     .fetch();


    // ============================================================================
    // 7. FIND WITH ORDERING
    // ============================================================================

    // JPA:
    // List<User> users = userRepository.findAll(Sort.by("name").ascending());

    // jOOQ:
    // var users = dsl.selectFrom(USER)
    //     .orderBy(USER.NAME.asc())
    //     .fetch();

    // // Multiple order columns
    // var users = dsl.selectFrom(USER)
    //     .orderBy(USER.ROLE.asc(), USER.NAME.asc())
    //     .fetch();


    // ============================================================================
    // 8. EXISTS CHECK
    // ============================================================================

    // JPA:
    // boolean exists = userRepository.existsById(1L);

    // jOOQ:
    // var exists = dsl.selectOne()
    //     .from(USER)
    //     .where(USER.ID.eq(1L))
    //     .fetchOne() != null;


    // ============================================================================
    // 9. COUNT RECORDS
    // ============================================================================

    // JPA:
    // long count = userRepository.count();

    // jOOQ:
    // var count = dsl.selectCount()
    //     .from(USER)
    //     .fetchOne(count());


    // ============================================================================
    // 10. DELETE RECORDS
    // ============================================================================

    // JPA:
    // userRepository.deleteById(1L);

    // jOOQ:
    // dsl.deleteFrom(USER)
    //     .where(USER.ID.eq(1L))
    //     .execute();


    // ============================================================================
    // 11. INSERT RECORD
    // ============================================================================

    // JPA:
    // User user = new User();
    // user.setEmail("new@example.com");
    // userRepository.save(user);

    // jOOQ:
    // dsl.insertInto(USER)
    //     .set(USER.EMAIL, "new@example.com")
    //     .execute();


    // ============================================================================
    // 12. UPDATE RECORD
    // ============================================================================

    // JPA:
    // User user = userRepository.findById(1L).orElse(null);
    // user.setName("Updated Name");
    // userRepository.save(user);

    // jOOQ:
    // dsl.update(USER)
    //     .set(USER.NAME, "Updated Name")
    //     .where(USER.ID.eq(1L))
    //     .execute();


    // ============================================================================
    // 13. COMPLEX QUERY WITH MULTIPLE CONDITIONS
    // ============================================================================

    // JPA:
    // @Query("SELECT u FROM User u WHERE u.verified = true " +
    //        "AND u.role IN ('ADMIN', 'SELLER') " +
    //        "AND u.name LIKE CONCAT('%', :name, '%')")
    // List<User> findAdminsOrSellersByName(@Param("name") String name);

    // jOOQ:
    // var results = dsl.selectFrom(USER)
    //     .where(USER.VERIFIED.eq(true))
    //     .and(USER.ROLE.in("ADMIN", "SELLER"))
    //     .and(USER.NAME.likeIgnoreCase("%" + name + "%"))
    //     .fetch();


    // ============================================================================
    // 14. ONE-TO-MANY JOIN (User with Addresses)
    // ============================================================================

    // JPA:
    // User user = userRepository.findById(1L).orElse(null);
    // List<Address> addresses = user.getAddresses(); // Lazy loading or N+1 issue

    // jOOQ:
    // var results = dsl.select(USER.asterisk(), ADDRESS.asterisk())
    //     .from(USER)
    //     .join(ADDRESS).on(ADDRESS.USER_ID.eq(USER.ID))
    //     .where(USER.ID.eq(1L))
    //     .fetch();


    // ============================================================================
    // 15. LEFT JOIN (Include users without addresses)
    // ============================================================================

    // jOOQ:
    // var results = dsl.select(USER.ID, USER.NAME, ADDRESS.STREET)
    //     .from(USER)
    //     .leftJoin(ADDRESS).on(ADDRESS.USER_ID.eq(USER.ID))
    //     .fetch();


    // ============================================================================
    // 16. AGGREGATION - GROUP BY
    // ============================================================================

    // JPA:
    // @Query("SELECT new map(u.role as role, count(u) as count) " +
    //        "FROM User u GROUP BY u.role")
    // List<Map<String, Object>> countByRole();

    // jOOQ:
    // var results = dsl.select(USER.ROLE, count())
    //     .from(USER)
    //     .groupBy(USER.ROLE)
    //     .fetch();


    // ============================================================================
    // 17. AGGREGATION - SUM, AVG, MAX, MIN
    // ============================================================================

    // jOOQ:
    // var stats = dsl.select(
    //     count().as("totalOrders"),
    //     sum(ORDER.TOTAL_AMOUNT).as("totalRevenue"),
    //     avg(ORDER.TOTAL_AMOUNT).as("avgOrderValue"),
    //     max(ORDER.TOTAL_AMOUNT).as("maxOrder"),
    //     min(ORDER.TOTAL_AMOUNT).as("minOrder")
    // ).from(ORDER)
    //     .fetch();


    // ============================================================================
    // 18. GROUP BY WITH HAVING
    // ============================================================================

    // jOOQ:
    // var products = dsl.select(PRODUCT.ID, PRODUCT.NAME, count(REVIEW.ID).as("reviewCount"))
    //     .from(PRODUCT)
    //     .leftJoin(REVIEW).on(REVIEW.PRODUCT_ID.eq(PRODUCT.ID))
    //     .groupBy(PRODUCT.ID, PRODUCT.NAME)
    //     .having(count(REVIEW.ID).greaterThan(5))  // Only products with 5+ reviews
    //     .fetch();


    // ============================================================================
    // 19. DISTINCT
    // ============================================================================

    // JPA:
    // @Query("SELECT DISTINCT u.role FROM User u")
    // List<String> findDistinctRoles();

    // jOOQ:
    // var roles = dsl.selectDistinct(USER.ROLE)
    //     .from(USER)
    //     .fetch();


    // ============================================================================
    // 20. BETWEEN
    // ============================================================================

    // jOOQ:
    // var products = dsl.selectFrom(PRODUCT)
    //     .where(PRODUCT.PRICE.between(10, 100))
    //     .fetch();


    // ============================================================================
    // 21. IN CLAUSE
    // ============================================================================

    // JPA:
    // List<User> users = userRepository.findByIdIn(Arrays.asList(1L, 2L, 3L));

    // jOOQ:
    // var users = dsl.selectFrom(USER)
    //     .where(USER.ID.in(1L, 2L, 3L))
    //     .fetch();


    // ============================================================================
    // 22. NOT IN CLAUSE
    // ============================================================================

    // jOOQ:
    // var users = dsl.selectFrom(USER)
    //     .where(USER.ID.notIn(1L, 2L, 3L))
    //     .fetch();


    // ============================================================================
    // 23. OR CONDITIONS
    // ============================================================================

    // jOOQ:
    // var users = dsl.selectFrom(USER)
    //     .where(USER.ROLE.eq("ADMIN"))
    //     .or(USER.VERIFIED.eq(true))
    //     .fetch();


    // ============================================================================
    // 24. CASE WHEN
    // ============================================================================

    // jOOQ:
    // var results = dsl.select(
    //     USER.ID,
    //     USER.NAME,
    //     DSL.choose()
    //         .when(USER.ROLE.eq("ADMIN"), DSL.val("Administrator"))
    //         .when(USER.ROLE.eq("SELLER"), DSL.val("Seller"))
    //         .otherwise(DSL.val("Customer"))
    //         .as("roleLabel")
    // ).from(USER)
    //     .fetch();


    // ============================================================================
    // 25. SUBQUERY
    // ============================================================================

    // jOOQ:
    // var avgPrice = dsl.select(avg(PRODUCT.PRICE))
    //     .from(PRODUCT)
    //     .asField();
    //
    // var expensiveProducts = dsl.selectFrom(PRODUCT)
    //     .where(PRODUCT.PRICE.greaterThan(avgPrice))
    //     .fetch();


    // ============================================================================
    // 26. MULTIPLE JOINs
    // ============================================================================

    // jOOQ:
    // var result = dsl.select(USER.ID, USER.NAME, PRODUCT.NAME, ORDER.STATUS)
    //     .from(USER)
    //     .join(ORDER).on(ORDER.USER_ID.eq(USER.ID))
    //     .join(ORDER_ITEM).on(ORDER_ITEM.ORDER_ID.eq(ORDER.ID))
    //     .join(PRODUCT).on(PRODUCT.ID.eq(ORDER_ITEM.PRODUCT_ID))
    //     .where(USER.VERIFIED.eq(true))
    //     .fetch();


    // ============================================================================
    // 27. BATCH INSERT
    // ============================================================================

    // JPA:
    // List<User> users = new ArrayList<>();
    // // ... populate users
    // userRepository.saveAll(users);

    // jOOQ:
    // var query = dsl.insertInto(USER)
    //     .columns(USER.NAME, USER.EMAIL, USER.ROLE);
    // for (var user : users) {
    //     query = query.values(user.getName(), user.getEmail(), user.getRole());
    // }
    // query.execute();


    // ============================================================================
    // 28. DYNAMIC WHERE CONDITIONS
    // ============================================================================

    // jOOQ:
    // Condition condition = DSL.noCondition();
    // if (name != null) {
    //     condition = condition.and(USER.NAME.likeIgnoreCase("%" + name + "%"));
    // }
    // if (role != null) {
    //     condition = condition.and(USER.ROLE.eq(role));
    // }
    // var users = dsl.selectFrom(USER)
    //     .where(condition)
    //     .fetch();


    // ============================================================================
    // 29. NULLS FIRST/LAST
    // ============================================================================

    // jOOQ:
    // var users = dsl.selectFrom(USER)
    //     .orderBy(USER.NAME.asc().nullsFirst())
    //     .fetch();


    // ============================================================================
    // 30. LIMIT WITH OFFSET (PAGINATION)
    // ============================================================================

    // jOOQ:
    // int page = 2;
    // int pageSize = 20;
    // var users = dsl.selectFrom(USER)
    //     .orderBy(USER.ID)
    //     .limit(pageSize)
    //     .offset(page * pageSize)
    //     .fetch();


}

