package com.quodex._miles.repository.jooq;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

/**
 * jOOQ User Repository Example
 *
 * This repository demonstrates how to use jOOQ for database queries.
 * It can coexist alongside Spring Data JPA repositories.
 *
 * Advantages of jOOQ over JPA:
 * - Type-safe SQL queries
 * - Better control over complex queries
 * - Easier to debug SQL
 * - Better performance for complex queries
 * - No N+1 query problems
 *
 * Example usage in services:
 * var user = userJooqRepository.findByEmail("user@example.com");
 * var users = userJooqRepository.findAll();
 */
@Repository
@RequiredArgsConstructor
@Slf4j
public class UserJooqRepository {

    private final DSLContext dsl;

    /**
     * Find user by email using jOOQ
     *
     * This example demonstrates a basic SELECT query with WHERE clause.
     * Replace USER table reference with the auto-generated jOOQ table class
     * after running: mvn clean compile jooq:generate
     */
    public void findByEmailExample(String email) {
        log.info("Searching for user with email: {}", email);

        // After running code generation, this will look like:
        // var user = dsl.selectFrom(USER)
        //     .where(USER.EMAIL.eq(email))
        //     .fetchOne();

        // For now, this is a placeholder showing the pattern
    }

    /**
     * Find all users using jOOQ
     */
    public void findAllExample() {
        log.info("Fetching all users");

        // After running code generation, this will look like:
        // var users = dsl.selectFrom(USER)
        //     .fetch();
    }

    /**
     * Find users with pagination
     */
    public void findWithPaginationExample(int limit, int offset) {
        log.info("Fetching users with limit: {} and offset: {}", limit, offset);

        // After running code generation, this will look like:
        // var users = dsl.selectFrom(USER)
        //     .limit(limit)
        //     .offset(offset)
        //     .fetch();
    }

    /**
     * Count users
     */
    public void countUsersExample() {
        log.info("Counting total users");

        // After running code generation, this will look like:
        // var count = dsl.selectCount()
        //     .from(USER)
        //     .fetchOne(count());
    }

    /**
     * Find users with complex WHERE conditions
     */
    public void findWithComplexConditionsExample(String name, boolean verified) {
        log.info("Finding users by name: {} and verified: {}", name, verified);

        // After running code generation, this will look like:
        // var users = dsl.selectFrom(USER)
        //     .where(USER.NAME.containsIgnoreCase(name))
        //     .and(USER.VERIFIED.eq(verified))
        //     .orderBy(USER.NAME)
        //     .fetch();
    }

    /**
     * Insert user example
     */
    public void insertUserExample(String userId, String email, String name) {
        log.info("Inserting user: {}", userId);

        // After running code generation, this will look like:
        // dsl.insertInto(USER)
        //     .set(USER.USER_ID, userId)
        //     .set(USER.EMAIL, email)
        //     .set(USER.NAME, name)
        //     .execute();
    }

    /**
     * Update user example
     */
    public void updateUserExample(Long userId, String name) {
        log.info("Updating user id: {}", userId);

        // After running code generation, this will look like:
        // dsl.update(USER)
        //     .set(USER.NAME, name)
        //     .where(USER.ID.eq(userId))
        //     .execute();
    }

    /**
     * Delete user example
     */
    public void deleteUserExample(Long userId) {
        log.info("Deleting user id: {}", userId);

        // After running code generation, this will look like:
        // dsl.deleteFrom(USER)
        //     .where(USER.ID.eq(userId))
        //     .execute();
    }

    /**
     * Complex JOIN query example
     */
    public void findUserWithAddressesExample(Long userId) {
        log.info("Fetching user with addresses for userId: {}", userId);

        // After running code generation, this will look like:
        // var result = dsl.select(USER.asterisk(), ADDRESS.asterisk())
        //     .from(USER)
        //     .leftJoin(ADDRESS).on(ADDRESS.USER_ID.eq(USER.ID))
        //     .where(USER.ID.eq(userId))
        //     .fetch();
    }

    /**
     * Aggregation query example
     */
    public void countUsersByRoleExample() {
        log.info("Counting users by role");

        // After running code generation, this will look like:
        // var result = dsl.select(USER.ROLE, count())
        //     .from(USER)
        //     .groupBy(USER.ROLE)
        //     .fetch();
    }
}

