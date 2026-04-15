# jOOQ Integration Guide for 28-Miles Backend

## Overview

jOOQ (Java Object Oriented Querying) is a lightweight ORM-like library that generates type-safe SQL queries. It complements Spring Data JPA by providing:

- **Type-safe SQL queries** - Compilation errors catch SQL mistakes early
- **Better control** - Fine-grained control over complex queries
- **Performance** - Optimized queries without N+1 problems
- **Readability** - SQL-like syntax that's familiar to developers
- **IDE Support** - IntelliSense for table and column names

## Project Structure

```
src/main/java/com/quodex/_miles/
├── config/
│   └── JooqConfig.java          # jOOQ DSLContext configuration
├── repository/
│   └── jooq/
│       └── UserJooqRepository.java   # Example jOOQ repository
├── util/
│   └── JooqUtil.java            # Common jOOQ utility methods
└── ... (existing code)
```

## Setup Instructions

### 1. Generate jOOQ Classes from Database

Before using jOOQ, you need to generate the typed classes from your database schema:

```bash
# Ensure your PostgreSQL database is running
# Update credentials in pom.xml if needed
mvn clean compile jooq:generate
```

This will generate classes in: `src/main/java/com/quodex/_miles/jooq/generated/`

The generated package contains:
- `Sequences.java` - Database sequences
- `Tables.java` - Table definitions
- `Keys.java` - Foreign key relationships
- Table classes for each table (e.g., `USER`, `PRODUCT`, `ORDER`, etc.)

### 2. Database Configuration

Ensure your database credentials in `pom.xml` match your running PostgreSQL:

```xml
<jdbc>
    <driver>org.postgresql.Driver</driver>
    <url>jdbc:postgresql://localhost:5432/28miles</url>
    <user>admin</user>
    <password>secret</password>
</jdbc>
```

## Basic Usage

### Import Generated Classes

```java
import static com.quodex._miles.jooq.generated.Tables.*;
import org.jooq.DSLContext;
```

### Inject DSLContext

```java
@Service
@RequiredArgsConstructor
public class UserService {
    private final DSLContext dsl;
    
    // Use dsl for queries
}
```

## Query Examples

### 1. Simple SELECT

```java
// Find user by ID
var user = dsl.selectFrom(USER)
    .where(USER.ID.eq(1L))
    .fetchOne();

// Get all users
var users = dsl.selectFrom(USER)
    .fetch();
```

### 2. SELECT with WHERE conditions

```java
// Multiple conditions
var users = dsl.selectFrom(USER)
    .where(USER.VERIFIED.eq(true))
    .and(USER.ROLE.eq(Role.ADMIN))
    .fetch();

// OR conditions
var users = dsl.selectFrom(USER)
    .where(USER.EMAIL.like("%@gmail.com%"))
    .or(USER.EMAIL.like("%@yahoo.com%"))
    .fetch();
```

### 3. SELECT with ORDER BY and LIMIT

```java
// Get top 10 most recent users
var users = dsl.selectFrom(USER)
    .orderBy(USER.ID.desc())
    .limit(10)
    .fetch();

// Pagination
var users = dsl.selectFrom(USER)
    .orderBy(USER.NAME)
    .limit(20)
    .offset(page * 20)
    .fetch();
```

### 4. JOINs

```java
// LEFT JOIN - Get users with their addresses
var result = dsl.select(USER.asterisk(), ADDRESS.asterisk())
    .from(USER)
    .leftJoin(ADDRESS).on(ADDRESS.USER_ID.eq(USER.ID))
    .fetch();

// INNER JOIN - Get only users with addresses
var result = dsl.select(USER.ID, USER.NAME, ADDRESS.STREET)
    .from(USER)
    .join(ADDRESS).on(ADDRESS.USER_ID.eq(USER.ID))
    .fetch();
```

### 5. Aggregations

```java
// Count users
var count = dsl.selectCount()
    .from(USER)
    .fetchOne(count());

// Count by role
var countByRole = dsl.select(USER.ROLE, count())
    .from(USER)
    .groupBy(USER.ROLE)
    .fetch();

// SUM, AVG, MAX, MIN
var stats = dsl.select(
    sum(ORDER.TOTAL_AMOUNT),
    avg(ORDER.TOTAL_AMOUNT),
    max(ORDER.TOTAL_AMOUNT)
).from(ORDER)
    .fetch();
```

### 6. INSERT

```java
// Single insert
dsl.insertInto(USER)
    .set(USER.USER_ID, "USR123")
    .set(USER.EMAIL, "user@example.com")
    .set(USER.NAME, "John Doe")
    .execute();

// Batch insert
var query = dsl.insertInto(USER)
    .columns(USER.USER_ID, USER.EMAIL, USER.NAME);

for (UserDTO user : users) {
    query = query.values(user.getUserId(), user.getEmail(), user.getName());
}
query.execute();
```

### 7. UPDATE

```java
// Update single user
dsl.update(USER)
    .set(USER.NAME, "Jane Doe")
    .set(USER.VERIFIED, true)
    .where(USER.ID.eq(1L))
    .execute();

// Update multiple records
dsl.update(USER)
    .set(USER.VERIFIED, true)
    .where(USER.EMAIL.like("%@verified.com%"))
    .execute();
```

### 8. DELETE

```java
// Delete single user
dsl.deleteFrom(USER)
    .where(USER.ID.eq(1L))
    .execute();

// Delete multiple records
dsl.deleteFrom(ORDER)
    .where(ORDER.STATUS.eq("CANCELLED"))
    .execute();
```

### 9. Complex Queries

```java
// Get products with review count and average rating
var result = dsl.select(
    PRODUCT.ID,
    PRODUCT.NAME,
    PRODUCT.PRICE,
    count(REVIEW.ID).as("reviewCount"),
    avg(REVIEW.RATING).as("avgRating")
).from(PRODUCT)
    .leftJoin(REVIEW).on(REVIEW.PRODUCT_ID.eq(PRODUCT.ID))
    .where(PRODUCT.CATEGORY_ID.eq(1L))
    .groupBy(PRODUCT.ID, PRODUCT.NAME, PRODUCT.PRICE)
    .orderBy(avg(REVIEW.RATING).desc())
    .fetch();

// Map to DTO
var productStats = result
    .map(record -> ProductStatDTO.builder()
        .id(record.get(PRODUCT.ID))
        .name(record.get(PRODUCT.NAME))
        .price(record.get(PRODUCT.PRICE))
        .reviewCount(record.get("reviewCount", Integer.class))
        .avgRating(record.get("avgRating", Double.class))
        .build())
    .toList();
```

## Advanced Features

### 1. Transactions

```java
@Transactional
public void complexOperation() {
    dsl.transaction(config -> {
        DSLContext tx = DSL.using(config);
        
        // Multiple operations within transaction
        tx.insertInto(ORDER).set(...).execute();
        tx.update(PRODUCT).set(...).execute();
        tx.deleteFrom(CART).where(...).execute();
    });
}
```

### 2. Raw SQL when needed

```java
// For very complex queries or specific database features
var result = dsl.resultQuery(
    "SELECT * FROM user WHERE email ILIKE ? AND verified = ?",
    "%gmail%", true
).fetch();
```

### 3. Dynamic Conditions

```java
Condition condition = DSL.noCondition();

if (filter.getRole() != null) {
    condition = condition.and(USER.ROLE.eq(filter.getRole()));
}
if (filter.getName() != null) {
    condition = condition.and(USER.NAME.containsIgnoreCase(filter.getName()));
}
if (filter.getVerified() != null) {
    condition = condition.and(USER.VERIFIED.eq(filter.getVerified()));
}

var users = dsl.selectFrom(USER)
    .where(condition)
    .fetch();
```

### 4. Case When

```java
var result = dsl.select(
    USER.ID,
    USER.NAME,
    DSL.choose()
        .when(USER.ROLE.eq(Role.ADMIN), DSL.val("Administrator"))
        .when(USER.ROLE.eq(Role.SELLER), DSL.val("Seller"))
        .otherwise(DSL.val("Customer"))
        .as("roleLabel")
).from(USER)
    .fetch();
```

## Migration from JPA to jOOQ

### Before (JPA):
```java
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    List<User> findByVerifiedTrue();
}

// Usage
var user = userRepository.findByEmail("user@example.com");
```

### After (jOOQ):
```java
@Repository
@RequiredArgsConstructor
public class UserJooqRepository {
    private final DSLContext dsl;
    
    public Optional<UserRecord> findByEmail(String email) {
        return dsl.selectFrom(USER)
            .where(USER.EMAIL.eq(email))
            .fetchOptional();
    }
    
    public List<UserRecord> findVerifiedUsers() {
        return dsl.selectFrom(USER)
            .where(USER.VERIFIED.eq(true))
            .fetch();
    }
}

// Usage
var user = userJooqRepository.findByEmail("user@example.com");
```

## Best Practices

1. **Keep both JPA and jOOQ** - Use JPA for simple CRUD, jOOQ for complex queries
2. **Create dedicated repositories** - Place jOOQ queries in separate repository classes
3. **Use static imports** - `import static com.quodex._miles.jooq.generated.Tables.*;`
4. **Handle null values** - Always check for Optional results
5. **Use transactions** - Wrap multi-step operations with `@Transactional`
6. **Log generated SQL** - Enable jOOQ logging for debugging:
   ```properties
   logging.level.org.jooq.tools.LoggerListener=DEBUG
   ```
7. **Regenerate when schema changes** - Run `mvn clean compile jooq:generate` after database schema updates

## Troubleshooting

### Generated classes not found
```bash
# Clean and regenerate
mvn clean compile jooq:generate
# Then do a project rebuild
```

### Database connection errors
- Verify PostgreSQL is running: `sudo service postgresql status`
- Check credentials in pom.xml
- Verify database exists: `createdb 28miles`

### Table not generated
- Check `excludes` pattern in pom.xml
- Verify table exists in database: `\dt` (in psql)
- Ensure proper permissions

## Additional Resources

- [jOOQ Official Documentation](https://www.jooq.org/doc/latest/manual/)
- [jOOQ Spring Boot Integration](https://spring.io/projects/spring-data-jooq)
- [jOOQ SQL Builder API](https://www.jooq.org/doc/latest/manual/sql-building/)

## Next Steps

1. Run code generation: `mvn clean compile jooq:generate`
2. Check generated classes in `src/main/java/com/quodex/_miles/jooq/generated/`
3. Implement `UserJooqRepository` with actual queries
4. Add similar repositories for other entities (Product, Order, etc.)
5. Update services to use jOOQ repositories for complex queries

