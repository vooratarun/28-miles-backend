# jOOQ Integration - Quick Start Guide

## ✅ What Has Been Done

Your Spring Boot project now has complete jOOQ integration with the following setup:

### 1. **Dependencies Added** (pom.xml)
   - jOOQ Core Library (v3.19.12)
   - jOOQ Code Generation plugin
   - PostgreSQL JDBC driver for code generation

### 2. **Configuration Created**
   - `JooqConfig.java` - Bean configuration providing DSLContext
   - `JooqUtil.java` - Utility component for common operations
   - `JOOQ_APPLICATION_PROPERTIES.txt` - Logging and configuration guide

### 3. **Example Implementations**
   - `UserJooqRepository.java` - Example patterns for all CRUD operations
   - `ProductJooqService.java` - Complex query examples (joins, aggregations)
   - `JOOQ_INTEGRATION_GUIDE.md` - Comprehensive documentation

---

## 🚀 Getting Started

### Step 1: Generate jOOQ Classes from Database

**Prerequisites:**
- PostgreSQL must be running
- Database `28miles` must exist with your tables
- Update credentials in `pom.xml` if they differ

**Run code generation:**
```bash
cd /home/tarv/Desktop/springboot/springboot/28-miles-backend
mvn clean compile jooq:generate
```

**Expected output:**
- Generated files will be created in: `src/main/java/com/quodex/_miles/jooq/generated/`
- Classes like: `Tables.java`, `USER.java`, `PRODUCT.java`, `ORDER.java`, etc.

### Step 2: Rebuild Project
```bash
mvn clean install
```

### Step 3: Start Using jOOQ

**Example 1: Simple Query**
```java
@Service
@RequiredArgsConstructor
public class UserService {
    private final DSLContext dsl;
    
    public User findByEmail(String email) {
        // Import: import static com.quodex._miles.jooq.generated.Tables.*;
        
        return dsl.selectFrom(USER)
            .where(USER.EMAIL.eq(email))
            .fetchOne()
            .map(record -> convertToUserDTO(record));
    }
}
```

**Example 2: Complex Query with Joins**
```java
public List<ProductStats> getProductsWithStats() {
    return dsl.select(
        PRODUCT.ID,
        PRODUCT.NAME,
        PRODUCT.PRICE,
        count(REVIEW.ID).as("reviewCount"),
        avg(REVIEW.RATING).as("avgRating")
    )
    .from(PRODUCT)
    .leftJoin(REVIEW).on(REVIEW.PRODUCT_ID.eq(PRODUCT.ID))
    .groupBy(PRODUCT.ID, PRODUCT.NAME, PRODUCT.PRICE)
    .orderBy(avg(REVIEW.RATING).desc())
    .fetch()
    .map(record -> ProductStats.builder()
        .id(record.get(PRODUCT.ID))
        .name(record.get(PRODUCT.NAME))
        .price(record.get(PRODUCT.PRICE))
        .reviewCount(record.get("reviewCount", Integer.class))
        .avgRating(record.get("avgRating", Double.class))
        .build())
    .toList();
}
```

---

## 📋 File Structure

```
28-miles-backend/
├── pom.xml                                      # ✅ Updated with jOOQ
│
├── src/main/java/com/quodex/_miles/
│   ├── config/
│   │   └── JooqConfig.java                     # ✅ New: jOOQ Configuration
│   │
│   ├── repository/
│   │   ├── UserRepository.java                 # Existing: Spring Data JPA
│   │   ├── ... (other JPA repositories)
│   │   │
│   │   └── jooq/
│   │       └── UserJooqRepository.java         # ✅ New: jOOQ Repository Example
│   │
│   ├── service/
│   │   ├── ... (existing services)
│   │   │
│   │   └── jooq/
│   │       └── ProductJooqService.java         # ✅ New: jOOQ Service Example
│   │
│   ├── util/
│   │   └── JooqUtil.java                       # ✅ New: jOOQ Utilities
│   │
│   └── jooq/
│       └── generated/                          # ✅ New: Auto-generated (after compilation)
│           ├── Tables.java
│           ├── USER.java
│           ├── PRODUCT.java
│           ├── ORDER.java
│           └── ... (all table classes)
│
├── JOOQ_INTEGRATION_GUIDE.md                   # ✅ New: Full Documentation
└── JOOQ_APPLICATION_PROPERTIES.txt             # ✅ New: Config Reference
```

---

## 🔧 Troubleshooting

### Issue: Database Connection Failed During Generation
```
Solution:
1. Verify PostgreSQL is running: sudo service postgresql status
2. Check database exists: psql -U admin -d 28miles -c "SELECT 1"
3. Verify credentials in pom.xml match your setup
4. Check database is accessible on localhost:5432
```

### Issue: Tables Not Generated
```
Solution:
1. Ensure tables exist in database: psql -U admin -d 28miles -c "\dt"
2. Check the excludes pattern in pom.xml (only system tables are excluded)
3. Try with specific schema: mvn jooq:generate -Djooq.schema=public
```

### Issue: Generated Classes Not Found
```
Solution:
1. Run: mvn clean compile jooq:generate
2. Project > Build Project (IDE)
3. In IntelliJ: File > Invalidate Caches > Invalidate and Restart
```

### Issue: Type Conflicts with JPA/Hibernate
```
Solution:
jOOQ and JPA can coexist. Use:
- JPA for simple CRUD operations
- jOOQ for complex queries
- Same datasource, no conflicts
```

---

## 📚 Next Steps

1. **Generate classes**: `mvn clean compile jooq:generate`
2. **Review generated files**: Check `src/main/java/com/quodex/_miles/jooq/generated/`
3. **Study examples**: Look at `UserJooqRepository.java` and `ProductJooqService.java`
4. **Implement queries**: Convert complex HQL/SQL to jOOQ
5. **Monitor performance**: Enable SQL logging to see generated queries

---

## 💡 Key Benefits

✅ **Type-Safe**: Compilation errors catch SQL mistakes  
✅ **IDE Support**: IntelliSense for column names  
✅ **No N+1 Problems**: Full control over queries  
✅ **Better Performance**: Optimized SQL generation  
✅ **Easy Debugging**: Generated SQL is readable  
✅ **Complex Queries**: Cleaner than JPA for joins/aggregations  
✅ **Coexists with JPA**: No migration needed, gradual adoption  

---

## 📖 Documentation Files

- **JOOQ_INTEGRATION_GUIDE.md** - Complete feature documentation
- **JOOQ_APPLICATION_PROPERTIES.txt** - Configuration options
- **UserJooqRepository.java** - All operation patterns
- **ProductJooqService.java** - Complex query examples

---

## ⚡ Quick Command Reference

```bash
# Generate jOOQ classes from database
mvn clean compile jooq:generate

# Full build with jOOQ generation
mvn clean install

# Skip jOOQ generation (use cached generated files)
mvn install -DskipJooq

# Show SQL queries (add to logging config)
logging.level.org.jooq.tools.LoggerListener=DEBUG

# Clean everything
mvn clean
```

---

## 🎯 Example Workflow

### Task: Find all verified users with orders

**JPA Approach (Complex)**:
```java
// Would require multiple queries or complex @Query annotation
List<User> users = userRepository.findByVerifiedTrue();
users.forEach(user -> user.getOrders()); // N+1 query problem!
```

**jOOQ Approach (Simple & Efficient)**:
```java
var users = dsl.select(USER.asterisk(), count(ORDER.ID).as("orderCount"))
    .from(USER)
    .leftJoin(ORDER).on(ORDER.USER_ID.eq(USER.ID))
    .where(USER.VERIFIED.eq(true))
    .groupBy(USER.ID)
    .having(count(ORDER.ID).greaterThan(0))
    .fetch();
// Single query, fully controlled
```

---

## 🔗 Resources

- [Official jOOQ Documentation](https://www.jooq.org/doc/latest/manual/)
- [jOOQ SQL API Reference](https://www.jooq.org/doc/latest/manual/sql-building/)
- [Spring Boot & jOOQ Integration](https://spring.io/projects/spring-data-jooq)

---

**You're all set! Start by running:** `mvn clean compile jooq:generate`

