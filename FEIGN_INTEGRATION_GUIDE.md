# Feign Client Integration Guide

## Overview

This project uses **Spring Cloud OpenFeign** to create declarative REST clients for integrating with external services. Feign simplifies HTTP client development by providing a way to define REST endpoints as Java interfaces.

## Architecture

### Project Structure

```
src/main/java/com/quodex/_miles/feign/
├── client/                    # Feign client interfaces
│   ├── ShippingServiceClient.java
│   ├── PaymentGatewayClient.java
│   ├── NotificationServiceClient.java
│   ├── *FeignConfig.java      # Configuration for each client
│   └── *ErrorDecoder.java     # Custom error handling
├── config/
│   └── FeignConfig.java       # Global Feign configuration
├── dto/                       # Request/Response DTOs
│   ├── ShippingQuoteRequest.java
│   ├── ShippingQuoteResponse.java
│   ├── PaymentVerificationRequest.java
│   ├── PaymentVerificationResponse.java
│   ├── RefundRequest.java
│   ├── RefundResponse.java
│   ├── EmailNotificationRequest.java
│   └── SmsNotificationRequest.java
└── service/                   # Business logic layer
    ├── PaymentFeignService.java
    ├── ShippingFeignService.java
    └── NotificationFeignService.java
```

## Feign Clients

### 1. Payment Gateway Client

**Purpose**: Integrate with external payment gateways (Razorpay, Stripe, PayPal, etc.)

```java
@FeignClient(
    name = "payment-gateway",
    url = "${payment.gateway.url:http://localhost:8082}",
    configuration = PaymentFeignConfig.class
)
```

**Endpoints**:
- POST `/api/payments/verify` - Verify payment
- GET `/api/payments/{paymentId}` - Get payment details
- POST `/api/refunds` - Initiate refund
- GET `/api/refunds/{refundId}` - Get refund status
- GET `/api/payments/{paymentId}/status` - Check payment status

### 2. Shipping Service Client

**Purpose**: Integrate with shipping providers (Shippo, EasyPost, DHL, etc.)

```java
@FeignClient(
    name = "shipping-service",
    url = "${shipping.service.url:http://localhost:8081}",
    configuration = ShippingFeignConfig.class
)
```

**Endpoints**:
- POST `/api/shipping/quotes` - Get shipping quote
- GET `/api/shipping/rates` - Get shipping rates
- POST `/api/shipping/labels` - Create shipping label
- GET `/api/shipping/track/{trackingId}` - Track shipment

### 3. Notification Service Client

**Purpose**: Send notifications (Email, SMS, Push) via external providers

```java
@FeignClient(
    name = "notification-service",
    url = "${notification.service.url:http://localhost:8083}",
    configuration = NotificationFeignConfig.class
)
```

**Endpoints**:
- POST `/api/notifications/email` - Send email
- POST `/api/notifications/sms` - Send SMS
- POST `/api/notifications/push` - Send push notification
- POST `/api/notifications/email/bulk` - Send bulk email

## Configuration

### Application Properties

Configure the external service URLs in `application.properties`:

```properties
# Feign Logging
feign.client.config.default.logger-level=FULL
logging.level.com.quodex._miles.feign.client=DEBUG

# Shipping Service
feign.client.config.shipping-service.url=http://localhost:8081
feign.client.config.shipping-service.connectTimeout=5000
feign.client.config.shipping-service.readTimeout=10000

# Payment Gateway
feign.client.config.payment-gateway.url=http://localhost:8082
feign.client.config.payment-gateway.connectTimeout=5000
feign.client.config.payment-gateway.readTimeout=15000

# Notification Service
feign.client.config.notification-service.url=http://localhost:8083
feign.client.config.notification-service.connectTimeout=3000
feign.client.config.notification-service.readTimeout=5000
```

### Enabling Feign Clients

In `Application.java`, add the `@EnableFeignClients` annotation:

```java
@SpringBootApplication
@EnableFeignClients(basePackages = "com.quodex._miles.feign.client")
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
```

## Key Features

### 1. **Declarative API Definitions**

Define REST clients as simple Java interfaces:

```java
@FeignClient(name = "payment-gateway", url = "${payment.gateway.url}")
public interface PaymentGatewayClient {
    @PostMapping("/api/payments/verify")
    PaymentVerificationResponse verifyPayment(@RequestBody PaymentVerificationRequest request);
}
```

### 2. **Custom Error Handling**

Each client has a custom `ErrorDecoder` for specific error handling:

```java
public class PaymentErrorDecoder implements ErrorDecoder {
    @Override
    public Exception decode(String methodKey, Response response) {
        if (response.status() == 402) {
            return new RuntimeException("Payment declined");
        }
        // ... more error handling
    }
}
```

### 3. **Logging and Debugging**

Configure different logging levels per client:

```properties
feign.client.config.payment-gateway.loggerLevel=FULL
feign.client.config.shipping-service.loggerLevel=FULL
feign.client.config.notification-service.loggerLevel=BASIC
```

**Logger Levels**:
- `NONE` - No logging (default)
- `BASIC` - Log only request/response line
- `HEADERS` - Log request/response headers and line
- `FULL` - Log request/response body and headers

### 4. **Timeout Configuration**

Set connection and read timeouts for each client:

```properties
feign.client.config.payment-gateway.connectTimeout=5000  # 5 seconds
feign.client.config.payment-gateway.readTimeout=15000    # 15 seconds
```

### 5. **Service Layer Integration**

Business logic services use Feign clients:

```java
@Service
@RequiredArgsConstructor
public class PaymentFeignService {
    private final PaymentGatewayClient paymentGatewayClient;
    
    public PaymentVerificationResponse verifyPayment(PaymentVerificationRequest request) {
        return paymentGatewayClient.verifyPayment(request);
    }
}
```

## Usage Examples

### Payment Verification

```bash
curl -X POST http://localhost:8080/api/v1.0/feign-demo/payments/verify \
  -H "Content-Type: application/json" \
  -d '{
    "paymentId": "pay_123",
    "signature": "sig_456",
    "orderId": "order_789",
    "amount": "999.99",
    "currency": "USD"
  }'
```

### Shipping Quote

```bash
curl -X POST http://localhost:8080/api/v1.0/feign-demo/shipping/quote \
  -H "Content-Type: application/json" \
  -d '{
    "fromZip": "10001",
    "toZip": "90210",
    "weight": 2.5,
    "serviceType": "express"
  }'
```

### Send Email Notification

```bash
curl -X POST http://localhost:8080/api/v1.0/feign-demo/notifications/email \
  -H "Content-Type: application/json" \
  -d '{
    "to": "user@example.com",
    "subject": "Order Confirmation",
    "body": "Your order has been confirmed"
  }'
```

## Error Handling

Each Feign client has custom error handling:

1. **PaymentErrorDecoder**: Handles payment-specific errors (402 Payment Required, etc.)
2. **ShippingErrorDecoder**: Handles shipping service errors
3. **NotificationErrorDecoder**: Handles notification service errors

### Example Error Response

```json
{
  "error": "Payment verification failed",
  "message": "Payment declined"
}
```

## Best Practices

### 1. **Always Use Service Layer**

Always wrap Feign clients in service classes for better maintainability:

```java
@Service
@RequiredArgsConstructor
public class PaymentFeignService {
    private final PaymentGatewayClient client;
    
    public PaymentVerificationResponse verifyPayment(PaymentVerificationRequest req) {
        // Add business logic, validation, logging here
        return client.verifyPayment(req);
    }
}
```

### 2. **Implement Circuit Breaker**

For production, consider adding Resilience4j or Spring Cloud Circuit Breaker:

```xml
<dependency>
    <groupId>io.github.resilience4j</groupId>
    <artifactId>resilience4j-spring-boot3</artifactId>
</dependency>
```

### 3. **Use Fallback Methods**

Implement fallback methods for resilience:

```java
@FeignClient(
    name = "payment-gateway",
    url = "${payment.gateway.url}",
    fallback = PaymentGatewayFallback.class
)
```

### 4. **Timeout Configuration**

Always set appropriate timeouts to prevent hanging requests:

```properties
feign.client.config.payment-gateway.connectTimeout=5000
feign.client.config.payment-gateway.readTimeout=15000
```

### 5. **Request/Response Logging**

Enable detailed logging for debugging:

```properties
logging.level.com.quodex._miles.feign.client=DEBUG
feign.client.config.default.logger-level=FULL
```

## Testing

### Mock Feign Clients for Testing

```java
@SpringBootTest
class PaymentServiceTest {
    
    @MockBean
    private PaymentGatewayClient paymentGatewayClient;
    
    @Test
    void testPaymentVerification() {
        PaymentVerificationResponse mockResponse = PaymentVerificationResponse.builder()
            .paymentId("pay_123")
            .status("captured")
            .build();
            
        when(paymentGatewayClient.verifyPayment(any()))
            .thenReturn(mockResponse);
            
        // Test logic
    }
}
```

## Common Issues and Solutions

### Issue 1: Feign Client Not Found

**Solution**: Ensure `@EnableFeignClients` is added to `Application.java` with correct basePackages.

### Issue 2: Connection Timeout

**Solution**: Increase timeout values in application.properties:

```properties
feign.client.config.payment-gateway.connectTimeout=10000
feign.client.config.payment-gateway.readTimeout=20000
```

### Issue 3: Authentication Failures

**Solution**: Add request interceptor in FeignConfig:

```java
@Bean
public BasicAuthRequestInterceptor basicAuthRequestInterceptor() {
    return new BasicAuthRequestInterceptor("username", "password");
}
```

## REST API Endpoints

### Feign Demo Controller Endpoints

**Base URL**: `http://localhost:8080/api/v1.0/feign-demo`

#### Payment Endpoints
- `POST /payments/verify` - Verify payment
- `GET /payments/{paymentId}` - Get payment details
- `POST /refunds` - Process refund
- `GET /payments/{paymentId}/status` - Check payment status

#### Shipping Endpoints
- `POST /shipping/quote` - Get shipping quote
- `GET /shipping/rates` - Get shipping rates
- `POST /shipping/labels` - Create shipping label
- `GET /shipping/track/{trackingId}` - Track shipment

#### Notification Endpoints
- `POST /notifications/email` - Send email
- `POST /notifications/sms` - Send SMS
- `POST /notifications/push` - Send push notification
- `POST /notifications/email/bulk` - Send bulk email

## Dependencies

```xml
<!-- Spring Cloud OpenFeign -->
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-openfeign</artifactId>
</dependency>

<!-- Spring Cloud BOM (for version management) -->
<dependencyManagement>
    <dependencies>
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-dependencies</artifactId>
            <version>2024.0.0</version>
            <type>pom</type>
            <scope>import</scope>
        </dependency>
    </dependencies>
</dependencyManagement>
```

## References

- [Spring Cloud OpenFeign Documentation](https://spring.io/projects/spring-cloud-openfeign)
- [Spring Cloud Release Trains](https://spring.io/projects/spring-cloud)
- [Feign GitHub Repository](https://github.com/OpenFeign/feign)

## Next Steps

1. Configure actual external service URLs in `application.properties`
2. Implement circuit breaker pattern using Resilience4j
3. Add authentication/authorization interceptors
4. Create comprehensive integration tests
5. Monitor Feign client metrics and performance

