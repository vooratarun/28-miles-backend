# jOOQ Common Pitfalls & Solutions

## 🚨 Pitfall 1: Forgetting Static Imports

### ❌ Wrong:
```java
var users = dsl.selectFrom(Tables.USER)
    .where(Tables.USER.ID.eq(1L))
    .fetch();
```

### ✅ Correct:
```java
import static com.quodex._miles.jooq.generated.Tables.*;

var users = dsl.selectFrom(USER)
    .where(USER.ID.eq(1L))
    .fetch();
```

**Why:** Static imports make queries more readable and follow jOOQ best practices.

---

## 🚨 Pitfall 2: Not Handling Null Results

### ❌ Wrong:
```java
var user = dsl.selectFrom(USER)
    .where(USER.ID.eq(1L))
    .fetchOne();  // Returns null if not found
System.out.println(user.getName());  // NullPointerException!
```

### ✅ Correct:
```java
var user = dsl.selectFrom(USER)
    .where(USER.ID.eq(1L))
    .fetchOptional()  // Returns Optional
    .map(record -> record.get(USER.NAME))
    .orElse(null);

// Or:
var user = dsl.selectFrom(USER)
    .where(USER.ID.eq(1L))
    .fetchOne();
if (user != null) {
    System.out.println(user.getName());
}
```

---

## 🚨 Pitfall 3: Missing GROUP BY Columns

### ❌ Wrong:
```java
var result = dsl.select(USER.ID, USER.NAME, count())
    .from(USER)
    .groupBy(USER.ID)  // ❌ Missing USER.NAME in GROUP BY
    .fetch();
```

**Error:** `column "user.name" must appear in the GROUP BY clause`

### ✅ Correct:
```java
var result = dsl.select(USER.ID, USER.NAME, count())
    .from(USER)
    .groupBy(USER.ID, USER.NAME)  // ✅ All non-aggregated columns
    .fetch();
```

---

## 🚨 Pitfall 4: Incorrect JOIN Syntax

### ❌ Wrong:
```java
var result = dsl.selectFrom(USER)
    .join(PRODUCT)  // ❌ Missing ON condition - will create CROSS JOIN
    .fetch();
```

### ✅ Correct:
```java
var result = dsl.selectFrom(USER)
    .join(ORDER).on(ORDER.USER_ID.eq(USER.ID))
    .join(ORDER_ITEM).on(ORDER_ITEM.ORDER_ID.eq(ORDER.ID))
    .join(PRODUCT).on(PRODUCT.ID.eq(ORDER_ITEM.PRODUCT_ID))
    .fetch();
```

---

## 🚨 Pitfall 5: Using Generated Column Names Instead of Field Objects

### ❌ Wrong:
```java
var result = dsl.select(USER.ID, USER.NAME, count().as("userCount"))
    .from(USER)
    .groupBy(USER.ID, USER.NAME)
    .fetch();

// Later trying to access:
var count = record.get("userCount", Long.class);  // ❌ Brittle, prone to typos
```

### ✅ Correct:
```java
var userCountField = count().as("userCount");
var result = dsl.select(USER.ID, USER.NAME, userCountField)
    .from(USER)
    .groupBy(USER.ID, USER.NAME)
    .fetch();

// Later accessing:
var count = record.get(userCountField);  // ✅ Type-safe!
```

---

## 🚨 Pitfall 6: Not Using Transactions for Multiple Operations

### ❌ Wrong:
```java
// If second operation fails, first one is committed
dsl.insertInto(ORDER).set(...).execute();
dsl.update(PRODUCT).set(...).execute();  // Partial data corruption
```

### ✅ Correct:
```java
@Transactional
public void processOrder(OrderDTO orderDTO) {
    dsl.transaction(config -> {
        DSLContext tx = DSL.using(config);
        
        // All operations succeed or all fail
        var orderId = tx.insertInto(ORDER)
            .set(...).execute();
        tx.update(PRODUCT).set(...).execute();
        tx.deleteFrom(CART).where(...).execute();
    });
}
```

---

## 🚨 Pitfall 7: Ignoring Database Column Names vs Java Field Names

### ❌ Wrong:
```java
// If column is "created_at" but trying to use "createdAt"
dsl.selectFrom(USER).where(USER.CREATED_AT.greaterThan(timestamp))
    .fetch();
// ❌ USER.CREATED_AT doesn't exist in generated table
```

### ✅ Correct:
```java
// Generated field names match database column names (with conversion)
// Database: created_at -> Generated field: CREATED_AT
// Database: user_id -> Generated field: USER_ID

dsl.selectFrom(USER)
    .where(USER.CREATED_AT.greaterThan(timestamp))
    .fetch();
```

---

## 🚨 Pitfall 8: LEFT JOIN with NULL Checks

### ❌ Wrong:
```java
var result = dsl.selectFrom(USER)
    .leftJoin(ADDRESS).on(ADDRESS.USER_ID.eq(USER.ID))
    .where(ADDRESS.STREET.eq("Main St"))  // ❌ Converts to INNER JOIN!
    .fetch();
```

### ✅ Correct:
```java
var result = dsl.selectFrom(USER)
    .leftJoin(ADDRESS).on(ADDRESS.USER_ID.eq(USER.ID))
    .where(ADDRESS.STREET.isNull()
        .or(ADDRESS.STREET.eq("Main St")))  // ✅ Proper NULL handling
    .fetch();

// Or use AND for filtering:
var result = dsl.selectFrom(USER)
    .leftJoin(ADDRESS).on(ADDRESS.USER_ID.eq(USER.ID)
        .and(ADDRESS.STREET.eq("Main St")))  // Join condition
    .fetch();
```

---

## 🚨 Pitfall 9: Type Mismatches in Field Updates

### ❌ Wrong:
```java
dsl.update(PRODUCT)
    .set(PRODUCT.PRICE, "99.99")  // ❌ String instead of BigDecimal
    .where(PRODUCT.ID.eq(1L))
    .execute();
```

### ✅ Correct:
```java
import java.math.BigDecimal;

dsl.update(PRODUCT)
    .set(PRODUCT.PRICE, new BigDecimal("99.99"))  // ✅ Correct type
    .where(PRODUCT.ID.eq(1L))
    .execute();
```

---

## 🚨 Pitfall 10: Mixing DSLContext Instances in Transactions

### ❌ Wrong:
```java
@Transactional
public void updateData() {
    dsl.transaction(config -> {
        DSLContext tx = DSL.using(config);
        
        // Using wrong context
        dsl.update(USER).set(...).execute();  // ❌ Outside transaction!
        tx.update(PRODUCT).set(...).execute();  // ✅ Inside transaction
    });
}
```

### ✅ Correct:
```java
@Transactional
public void updateData() {
    dsl.transaction(config -> {
        DSLContext tx = DSL.using(config);
        
        // Use consistent context
        tx.update(USER).set(...).execute();
        tx.update(PRODUCT).set(...).execute();
    });
}
```

---

## 🚨 Pitfall 11: IN Clause with Empty Lists

### ❌ Wrong:
```java
List<Long> ids = new ArrayList<>();  // Empty!
var users = dsl.selectFrom(USER)
    .where(USER.ID.in(ids))  // ❌ Database error or unexpected behavior
    .fetch();
```

### ✅ Correct:
```java
List<Long> ids = getIds();
if (ids.isEmpty()) {
    return Collections.emptyList();
}
var users = dsl.selectFrom(USER)
    .where(USER.ID.in(ids))
    .fetch();

// Or with noCondition:
var users = dsl.selectFrom(USER)
    .where(ids.isEmpty() ? DSL.noCondition() : USER.ID.in(ids))
    .fetch();
```

---

## 🚨 Pitfall 12: Case Sensitivity in Column References

### ❌ Wrong:
```java
// This might work in some databases but not others
dsl.selectFrom(user)  // ❌ Lowercase
    .fetch();

// Or:
dsl.selectFrom(User)  // ❌ CamelCase
    .fetch();
```

### ✅ Correct:
```java
// Always use UPPERCASE (database convention)
import static com.quodex._miles.jooq.generated.Tables.*;

dsl.selectFrom(USER)  // ✅ UPPERCASE - generated constant
    .fetch();
```

---

## 🚨 Pitfall 13: Not Regenerating After Schema Changes

### ❌ Wrong:
```
1. Database schema changed (new column added)
2. Continue using old generated classes
3. get(PRODUCT.NEW_COLUMN) throws error
```

### ✅ Correct:
```
1. Database schema changed
2. Run: mvn clean compile jooq:generate
3. Rebuild project
4. Use new generated classes
```

---

## 🚨 Pitfall 14: Aggregate Functions Without Grouping

### ❌ Wrong:
```java
var result = dsl.select(count(USER.ID), USER.NAME)  // ❌ No GROUP BY
    .from(USER)
    .fetch();
```

### ✅ Correct:
```java
// Option 1: If you need name with count
var result = dsl.select(count(USER.ID), USER.NAME)
    .from(USER)
    .groupBy(USER.NAME)
    .fetch();

// Option 2: If you just want total count
var totalCount = dsl.selectCount()
    .from(USER)
    .fetchOne(count());
```

---

## 🚨 Pitfall 15: DSLContext Not Injected

### ❌ Wrong:
```java
@Service
public class UserService {
    // ❌ Forgot to inject!
    private DSLContext dsl;
    
    public void getUsers() {
        dsl.selectFrom(USER).fetch();  // NullPointerException!
    }
}
```

### ✅ Correct:
```java
@Service
@RequiredArgsConstructor  // ✅ Lombok auto-wires final fields
public class UserService {
    private final DSLContext dsl;  // ✅ Must be final with @RequiredArgsConstructor
    
    public void getUsers() {
        dsl.selectFrom(USER).fetch();  // ✅ Works!
    }
}

// Or:
@Service
public class UserService {
    @Autowired
    private DSLContext dsl;  // ✅ Spring injection
    
    public void getUsers() {
        dsl.selectFrom(USER).fetch();  // ✅ Works!
    }
}
```

---

## 🚨 Pitfall 16: Forgetting Import for static constants

### ❌ Wrong:
```java
@Service
public class UserService {
    @Autowired
    private DSLContext dsl;
    
    public void getUsers() {
        dsl.selectFrom(USER).fetch();  // ❌ USER not recognized (no import)
    }
}
```

### ✅ Correct:
```java
import static com.quodex._miles.jooq.generated.Tables.*;

@Service
public class UserService {
    @Autowired
    private DSLContext dsl;
    
    public void getUsers() {
        dsl.selectFrom(USER).fetch();  // ✅ USER is now available
    }
}
```

---

## 🚨 Pitfall 17: Performance Issues with N+1 Queries

### ❌ Wrong (causes N+1):
```java
var users = jpaUserRepository.findAll();
users.forEach(user -> {
    var addresses = user.getAddresses();  // ❌ Separate query for each user!
    process(addresses);
});
```

### ✅ Correct (Single query):
```java
var results = dsl.select(USER.asterisk(), ADDRESS.asterisk())
    .from(USER)
    .leftJoin(ADDRESS).on(ADDRESS.USER_ID.eq(USER.ID))
    .fetch();
// ✅ Single query, process results in memory
```

---

## 🚨 Pitfall 18: String Concatenation Instead of Parameters

### ❌ Wrong (SQL Injection):
```java
String keyword = getUserInput();
var products = dsl.selectFrom(PRODUCT)
    .where(PRODUCT.NAME.like("%" + keyword + "%"))  // ❌ Vulnerable!
    .fetch();
```

### ✅ Correct (Parameterized):
```java
String keyword = getUserInput();
var products = dsl.selectFrom(PRODUCT)
    .where(PRODUCT.NAME.likeIgnoreCase("%" + keyword + "%"))  // Still OK (escaped by jOOQ)
    .fetch();

// Or safer:
var placeholder = DSL.val("%" + keyword + "%");
var products = dsl.selectFrom(PRODUCT)
    .where(PRODUCT.NAME.like(placeholder))
    .fetch();
```

---

## 🚨 Pitfall 19: Unexpected NULL Handling in Conditions

### ❌ Wrong:
```java
Long userId = null;  // Could be null
var orders = dsl.selectFrom(ORDER)
    .where(ORDER.USER_ID.eq(userId))  // ❌ Never matches NULL values!
    .fetch();
```

### ✅ Correct:
```java
Long userId = null;  // Could be null
Condition condition = userId == null 
    ? ORDER.USER_ID.isNull()
    : ORDER.USER_ID.eq(userId);

var orders = dsl.selectFrom(ORDER)
    .where(condition)
    .fetch();
```

---

## 🚨 Pitfall 20: Configuration File Not Matching pom.xml

### ❌ Wrong:
If `pom.xml` has:
```xml
<url>jdbc:postgresql://localhost:5432/28miles</url>
```

But `application.properties` has:
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/other_db
```

The code generation uses `pom.xml` URL, so generated classes won't match your runtime database.

### ✅ Correct:
```
Keep both in sync or use:
1. Profile-specific properties
2. Environment variables
3. External configuration
```

---

## 📝 Summary Checklist

Before deploying jOOQ code, verify:

- [ ] All imports are static: `import static ...Tables.*;`
- [ ] DSLContext is properly injected
- [ ] NULL results are handled with `fetchOptional()` or null checks
- [ ] GROUP BY includes all non-aggregated columns
- [ ] JOINs have ON conditions (no CROSS JOINs)
- [ ] Column names are UPPERCASE (generated constants)
- [ ] Types match (BigDecimal for money, etc.)
- [ ] Transactions wrap multi-step operations
- [ ] Generated classes were regenerated after schema changes
- [ ] No string concatenation in WHERE conditions
- [ ] Empty lists are handled before IN clauses
- [ ] LEFT JOINs properly handle NULL values

---

## 🔗 References

- See `JOOQ_INTEGRATION_GUIDE.md` for comprehensive documentation
- See `JpaToJooqExamples.java` for 30 query patterns
- See `UserJooqRepository.java` for practical examples

