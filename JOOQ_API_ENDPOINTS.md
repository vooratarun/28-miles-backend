# jOOQ Demo API Endpoints

## Overview

This document describes the jOOQ demo API endpoints that showcase various query patterns using type-safe jOOQ instead of JPA/Hibernate.

**Base URL:** `http://localhost:8080/api/v1.0/jooq-demo`

All endpoints return JSON responses and demonstrate different jOOQ capabilities.

---

## 🏥 Health Check

### GET `/jooq-demo/health`
Verify jOOQ integration is working.

**Response:**
```json
"jOOQ integration is healthy! Total users: 42"
```

---

## 📊 Statistics Endpoints

### GET `/jooq-demo/stats/users`
Get comprehensive user statistics.

**Demonstrates:** Aggregation queries, GROUP BY, conditional counting

**Response:**
```json
{
  "totalUsers": 150,
  "verifiedUsers": 120,
  "adminUsers": 5,
  "sellerUsers": 15,
  "customerUsers": 130
}
```

### GET `/jooq-demo/stats/categories`
Get category statistics with product counts and price ranges.

**Demonstrates:** Multiple aggregations (COUNT, SUM, AVG, MIN, MAX), LEFT JOIN

**Response:**
```json
[
  {
    "categoryId": "1",
    "categoryName": "T-Shirts",
    "productCount": 25,
    "totalStock": 500,
    "averagePrice": 29.99,
    "minPrice": 15.99,
    "maxPrice": 49.99
  }
]
```

### GET `/jooq-demo/dashboard/stats`
Get comprehensive dashboard statistics.

**Demonstrates:** Multiple parallel queries, complex aggregations

**Response:**
```json
{
  "totalUsers": 150,
  "totalProducts": 200,
  "totalOrders": 350,
  "totalCategories": 12,
  "totalRevenue": 45000.00,
  "pendingOrders": 15,
  "completedOrders": 335,
  "averageOrderValue": 128.57
}
```

---

## 📈 Analytics Endpoints

### GET `/jooq-demo/analytics/products`
Get product analytics with reviews and sales data.

**Demonstrates:** Complex multi-table JOINs, conditional aggregations

**Response:**
```json
[
  {
    "productId": "PROD-001",
    "productName": "Classic White T-Shirt",
    "price": 24.99,
    "reviewCount": 15,
    "averageRating": 4.2,
    "orderCount": 45,
    "totalRevenue": 1124.55
  }
]
```

### GET `/jooq-demo/products/top-selling?limit=5`
Get top-selling products by quantity sold.

**Demonstrates:** Complex JOINs with ORDER_ITEM and ORDER tables, filtering by status

**Parameters:**
- `limit` (optional): Number of products to return (default: 5)

**Response:**
```json
[
  {
    "productId": "PROD-001",
    "productName": "Classic White T-Shirt",
    "price": 24.99,
    "orderCount": 45,
    "totalRevenue": 1124.55
  }
]
```

---

## 🔍 Search & Filter Endpoints

### GET `/jooq-demo/users/search`
Search users with flexible criteria.

**Demonstrates:** Dynamic WHERE conditions, case-insensitive LIKE, optional parameters

**Parameters (all optional):**
- `name`: Filter by name (partial match)
- `email`: Filter by email (partial match)
- `role`: Filter by exact role (ADMIN, SELLER, USER)
- `verified`: Filter by verification status (true/false)

**Examples:**
```
GET /jooq-demo/users/search?name=john
GET /jooq-demo/users/search?email=gmail&verified=true
GET /jooq-demo/users/search?role=ADMIN
GET /jooq-demo/users/search?name=john&email=gmail&role=USER&verified=true
```

**Response:**
```json
[
  {
    "userId": "USR-12345",
    "name": "John Doe",
    "email": "john.doe@gmail.com",
    "phone": "+1234567890",
    "role": "USER"
  }
]
```

### GET `/jooq-demo/products/price-range`
Get products within a price range.

**Demonstrates:** BETWEEN queries, conditional filtering, optional parameters

**Parameters (all optional):**
- `categoryId`: Filter by category
- `minPrice`: Minimum price filter
- `maxPrice`: Maximum price filter

**Examples:**
```
GET /jooq-demo/products/price-range?minPrice=50&maxPrice=200
GET /jooq-demo/products/price-range?categoryId=1&minPrice=20
GET /jooq-demo/products/price-range?maxPrice=100
```

**Response:**
```json
[
  {
    "productId": "PROD-001",
    "name": "Classic White T-Shirt",
    "price": 24.99,
    "categoryName": "T-Shirts",
    "stock": 150
  }
]
```

---

## 📋 Order Endpoints

### GET `/jooq-demo/orders/recent?limit=10`
Get recent orders with user details.

**Demonstrates:** Multiple JOINs, GROUP BY with COUNT, pagination

**Parameters:**
- `limit` (optional): Number of orders to return (default: 10)

**Response:**
```json
[
  {
    "orderId": "ORD-001",
    "userId": "USR-12345",
    "userName": "John Doe",
    "userEmail": "john.doe@gmail.com",
    "totalAmount": 149.97,
    "status": "COMPLETED",
    "itemCount": 3
  }
]
```

---

## 🧪 Testing the Endpoints

### Using cURL

```bash
# Health check
curl http://localhost:8080/api/v1.0/jooq-demo/health

# User statistics
curl http://localhost:8080/api/v1.0/jooq-demo/stats/users

# Product analytics
curl http://localhost:8080/api/v1.0/jooq-demo/analytics/products

# Search users
curl "http://localhost:8080/api/v1.0/jooq-demo/users/search?name=john&verified=true"

# Price range filter
curl "http://localhost:8080/api/v1.0/jooq-demo/products/price-range?minPrice=20&maxPrice=100"

# Recent orders
curl "http://localhost:8080/api/v1.0/jooq-demo/orders/recent?limit=5"

# Dashboard stats
curl http://localhost:8080/api/v1.0/jooq-demo/dashboard/stats
```

### Using Postman/Browser

Open these URLs in your browser or import into Postman:

```
http://localhost:8080/api/v1.0/jooq-demo/health
http://localhost:8080/api/v1.0/jooq-demo/stats/users
http://localhost:8080/api/v1.0/jooq-demo/analytics/products
http://localhost:8080/api/v1.0/jooq-demo/stats/categories
http://localhost:8080/api/v1.0/jooq-demo/dashboard/stats
http://localhost:8080/api/v1.0/jooq-demo/orders/recent?limit=5
http://localhost:8080/api/v1.0/jooq-demo/products/top-selling?limit=3
```

### Search Examples

```
# Find all verified users with "john" in name
http://localhost:8080/api/v1.0/jooq-demo/users/search?name=john&verified=true

# Find admin users
http://localhost:8080/api/v1.0/jooq-demo/users/search?role=ADMIN

# Find users with Gmail addresses
http://localhost:8080/api/v1.0/jooq-demo/users/search?email=gmail

# Products between $50-$200
http://localhost:8080/api/v1.0/jooq-demo/products/price-range?minPrice=50&maxPrice=200

# Products in category 1, under $100
http://localhost:8080/api/v1.0/jooq-demo/products/price-range?categoryId=1&maxPrice=100
```

---

## 🔧 Technical Details

### jOOQ Features Demonstrated

| Endpoint | jOOQ Features Used |
|----------|-------------------|
| `/stats/users` | `count()`, `groupBy()`, `filterWhere()` |
| `/analytics/products` | Multi-table `JOIN`, `leftJoin()`, aggregations |
| `/stats/categories` | Multiple aggregations, `min()`, `max()`, `avg()` |
| `/orders/recent` | Multiple `JOIN`, `groupBy()`, `limit()`, `orderBy()` |
| `/dashboard/stats` | Parallel queries, complex aggregations |
| `/users/search` | Dynamic conditions, `likeIgnoreCase()`, optional filters |
| `/products/price-range` | `between()`, conditional `where()` clauses |
| `/products/top-selling` | Complex JOINs, `sum()`, `count()`, filtering |

### Performance Benefits

- **Single queries** instead of N+1 problems
- **Type-safe** - compilation errors catch SQL mistakes
- **Optimized SQL** - jOOQ generates efficient queries
- **No lazy loading** - all data fetched in one go
- **Database indexes** can be utilized effectively

### Error Handling

All endpoints include proper logging and error handling. Check application logs for detailed query execution information.

---

## 📚 Related Documentation

- **JOOQ_QUICKSTART.md** - Setup instructions
- **JOOQ_INTEGRATION_GUIDE.md** - Complete reference
- **JOOQ_COMMON_PITFALLS.md** - Mistakes to avoid
- **JpaToJooqExamples.java** - 30 query patterns

---

## 🚀 Next Steps

1. **Test the endpoints** using the examples above
2. **Check the generated SQL** in application logs (enable DEBUG logging)
3. **Compare performance** with equivalent JPA queries
4. **Extend the patterns** for your specific use cases
5. **Add more endpoints** following the same patterns

---

**Ready to explore?** Start with the health check endpoint:

`GET http://localhost:8080/api/v1.0/jooq-demo/health`

