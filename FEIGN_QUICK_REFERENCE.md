# Feign Client Quick Reference

## Table of Contents

1. [Feign Clients](#feign-clients)
2. [Configuration](#configuration)
3. [API Endpoints](#api-endpoints)
4. [Common Tasks](#common-tasks)
5. [Troubleshooting](#troubleshooting)

## Feign Clients

### 1. Payment Gateway Client

**Client Interface**: `PaymentGatewayClient.java`
**Service Class**: `PaymentFeignService.java`
**Config Class**: `PaymentFeignConfig.java`
**Error Decoder**: `PaymentErrorDecoder.java`

**Methods**:
```java
PaymentVerificationResponse verifyPayment(PaymentVerificationRequest request)
PaymentVerificationResponse getPaymentDetails(String paymentId)
RefundResponse initiateRefund(RefundRequest request)
RefundResponse getRefundStatus(String refundId)
String getPaymentStatus(String paymentId)
```

### 2. Shipping Service Client

**Client Interface**: `ShippingServiceClient.java`
**Service Class**: `ShippingFeignService.java`
**Config Class**: `ShippingFeignConfig.java`
**Error Decoder**: `ShippingErrorDecoder.java`

**Methods**:
```java
ShippingQuoteResponse getShippingQuote(ShippingQuoteRequest request)
List<ShippingQuoteResponse> getShippingRates(String fromZip, String toZip, Double weight)
ShippingQuoteResponse createShippingLabel(ShippingQuoteRequest request)
ShippingQuoteResponse trackShipment(String trackingId)
```

### 3. Notification Service Client

**Client Interface**: `NotificationServiceClient.java`
**Service Class**: `NotificationFeignService.java`
**Config Class**: `NotificationFeignConfig.java`
**Error Decoder**: `NotificationErrorDecoder.java`

**Methods**:
```java
void sendEmail(EmailNotificationRequest request)
void sendSms(SmsNotificationRequest request)
void sendPushNotification(SmsNotificationRequest request)
void sendBulkEmail(EmailNotificationRequest request)
```

## Configuration

### application.properties

```properties
# Global Feign Configuration
feign.client.config.default.logger-level=FULL

# Payment Gateway
feign.client.config.payment-gateway.url=http://localhost:8082
feign.client.config.payment-gateway.connectTimeout=5000
feign.client.config.payment-gateway.readTimeout=15000
feign.client.config.payment-gateway.loggerLevel=FULL

# Shipping Service
feign.client.config.shipping-service.url=http://localhost:8081
feign.client.config.shipping-service.connectTimeout=5000
feign.client.config.shipping-service.readTimeout=10000
feign.client.config.shipping-service.loggerLevel=FULL

# Notification Service
feign.client.config.notification-service.url=http://localhost:8083
feign.client.config.notification-service.connectTimeout=3000
feign.client.config.notification-service.readTimeout=5000
feign.client.config.notification-service.loggerLevel=BASIC

# Logging
logging.level.com.quodex._miles.feign.client=DEBUG
```

## API Endpoints

### Base Path
```
http://localhost:8080/api/v1.0/feign-demo
```

### Payment Endpoints

| Method | Endpoint | Description | Request | Response |
|--------|----------|-------------|---------|----------|
| POST | `/payments/verify` | Verify payment | `PaymentVerificationRequest` | `PaymentVerificationResponse` |
| GET | `/payments/{paymentId}` | Get payment details | Path param: paymentId | `PaymentVerificationResponse` |
| POST | `/refunds` | Process refund | `RefundRequest` | `RefundResponse` |
| GET | `/payments/{paymentId}/status` | Check payment status | Path param: paymentId | `String` |

### Shipping Endpoints

| Method | Endpoint | Description | Request | Response |
|--------|----------|-------------|---------|----------|
| POST | `/shipping/quote` | Get shipping quote | `ShippingQuoteRequest` | `ShippingQuoteResponse` |
| GET | `/shipping/rates` | Get shipping rates | Query params: fromZip, toZip, weight | `List<ShippingQuoteResponse>` |
| POST | `/shipping/labels` | Create shipping label | `ShippingQuoteRequest` | `ShippingQuoteResponse` |
| GET | `/shipping/track/{trackingId}` | Track shipment | Path param: trackingId | `ShippingQuoteResponse` |

### Notification Endpoints

| Method | Endpoint | Description | Request | Response |
|--------|----------|-------------|---------|----------|
| POST | `/notifications/email` | Send email | `EmailNotificationRequest` | `String` (success message) |
| POST | `/notifications/sms` | Send SMS | `SmsNotificationRequest` | `String` (success message) |
| POST | `/notifications/push` | Send push notification | `SmsNotificationRequest` | `String` (success message) |
| POST | `/notifications/email/bulk` | Send bulk email | `EmailNotificationRequest` | `String` (success message) |

## Common Tasks

### Task 1: Add a New Feign Client

```java
@FeignClient(
    name = "new-service",
    url = "${new.service.url:http://localhost:8080}",
    configuration = NewServiceFeignConfig.class
)
public interface NewServiceClient {
    @PostMapping("/api/endpoint")
    ResponseDTO callEndpoint(@RequestBody RequestDTO request);
}
```

### Task 2: Add Authentication to Feign Client

```java
@Component
public class AuthRequestInterceptor implements RequestInterceptor {
    @Override
    public void apply(RequestTemplate template) {
        template.header("Authorization", "Bearer " + getToken());
    }
}
```

### Task 3: Handle Errors from Feign Client

```java
@Slf4j
public class CustomErrorDecoder implements ErrorDecoder {
    @Override
    public Exception decode(String methodKey, Response response) {
        log.error("Error {}: {}", response.status(), response.reason());
        return new RuntimeException("API Error");
    }
}
```

### Task 4: Log Feign Client Requests

Add to `application.properties`:
```properties
logging.level.com.quodex._miles.feign.client=DEBUG
feign.client.config.default.logger-level=FULL
```

### Task 5: Retry Failed Requests

```java
@Configuration
public class FeignConfig {
    @Bean
    public Retryer retryer() {
        return new Retryer.Default(100, 1000, 3);
    }
}
```

### Task 6: Inject Feign Client in Service

```java
@Service
@RequiredArgsConstructor
public class MyService {
    private final PaymentGatewayClient paymentClient;
    
    public void processPayment(PaymentVerificationRequest request) {
        paymentClient.verifyPayment(request);
    }
}
```

### Task 7: Mock Feign Client in Tests

```java
@SpringBootTest
class MyServiceTest {
    @MockBean
    private PaymentGatewayClient paymentClient;
    
    @Test
    void testPayment() {
        when(paymentClient.verifyPayment(any())).thenReturn(mockResponse);
        // Test logic
    }
}
```

## Troubleshooting

### Problem: Connection Refused

**Symptom**: `Connection refused to host`

**Solution**:
1. Check if external service is running
2. Verify correct URL in `application.properties`
3. Increase timeout values

```properties
feign.client.config.service-name.connectTimeout=10000
feign.client.config.service-name.readTimeout=20000
```

### Problem: 404 Not Found

**Symptom**: `HTTP 404 - Not Found`

**Solution**:
1. Verify endpoint path
2. Check if service is running at correct URL
3. Use curl to test the endpoint manually

```bash
curl -v http://external-service:8080/api/endpoint
```

### Problem: 401 Unauthorized

**Symptom**: `HTTP 401 - Unauthorized`

**Solution**:
1. Add authentication header
2. Use request interceptor
3. Pass authorization token

```java
@Override
public void apply(RequestTemplate template) {
    template.header("Authorization", "Bearer " + token);
}
```

### Problem: Timeout Exception

**Symptom**: `Connection timeout`

**Solution**:
1. Increase timeout values
2. Check if service is responsive
3. Add circuit breaker pattern

```properties
feign.client.config.service-name.connectTimeout=10000
feign.client.config.service-name.readTimeout=30000
```

### Problem: Feign Client Not Injected

**Symptom**: `NoSuchBeanDefinitionException`

**Solution**:
1. Ensure `@EnableFeignClients` is present
2. Verify correct base package
3. Check if service starts without errors

```java
@SpringBootApplication
@EnableFeignClients(basePackages = "com.quodex._miles.feign.client")
public class Application {
}
```

### Problem: Wrong Content-Type

**Symptom**: `400 Bad Request - Content-Type mismatch`

**Solution**:
1. Add content type header in interceptor
2. Verify DTO has correct annotations
3. Check @RequestBody annotation

```java
@Override
public void apply(RequestTemplate template) {
    template.header("Content-Type", "application/json");
    template.header("Accept", "application/json");
}
```

### Problem: Response Deserialization Error

**Symptom**: `JsonMappingException`

**Solution**:
1. Verify DTO matches API response
2. Use @JsonProperty for field mapping
3. Add @JsonIgnoreProperties(ignoreUnknown = true)

```java
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ResponseDTO {
    @JsonProperty("response_field")
    private String fieldName;
}
```

## Environment-Specific Configuration

### Development
```properties
feign.client.config.payment-gateway.url=http://localhost:8082
feign.client.config.default.logger-level=FULL
```

### Staging
```properties
feign.client.config.payment-gateway.url=https://staging-api.example.com
feign.client.config.default.logger-level=BASIC
```

### Production
```properties
feign.client.config.payment-gateway.url=https://api.example.com
feign.client.config.default.logger-level=NONE
```

## Performance Tips

1. **Reduce Logging**: Set logger-level to NONE in production
2. **Connection Pooling**: Configure OkHttpClient with connection pool
3. **Timeouts**: Set appropriate timeouts based on service response time
4. **Caching**: Implement caching for frequently called endpoints
5. **Circuit Breaker**: Add circuit breaker for fault tolerance

## Additional Resources

- [Feign GitHub](https://github.com/OpenFeign/feign)
- [Spring Cloud Feign Docs](https://spring.io/projects/spring-cloud-openfeign)
- [Spring Cloud Release Notes](https://spring.io/projects/spring-cloud)

---

**Last Updated**: April 15, 2026
**Version**: 1.0

