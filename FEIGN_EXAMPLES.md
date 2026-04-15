# Feign Client Implementation Examples

## Quick Start Guide

### 1. Adding Feign Dependency

The project already includes:

```xml
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-openfeign</artifactId>
</dependency>
```

### 2. Enable Feign Clients

The Application.java is already configured:

```java
@SpringBootApplication
@EnableFeignClients(basePackages = "com.quodex._miles.feign.client")
public class Application {
    // ...
}
```

### 3. Configure External Service URLs

Update `application.properties`:

```properties
feign.client.config.payment-gateway.url=https://api.payment-provider.com
feign.client.config.shipping-service.url=https://api.shipping-provider.com
feign.client.config.notification-service.url=https://api.notification-provider.com
```

## File Structure Overview

```
src/main/java/com/quodex/_miles/feign/
│
├── client/                                    # Feign Client Interfaces
│   ├── PaymentGatewayClient.java             # Payment gateway REST client
│   ├── ShippingServiceClient.java            # Shipping provider REST client
│   ├── NotificationServiceClient.java        # Notification service REST client
│   ├── PaymentFeignConfig.java               # Payment client config
│   ├── ShippingFeignConfig.java              # Shipping client config
│   ├── NotificationFeignConfig.java          # Notification client config
│   ├── PaymentErrorDecoder.java              # Payment error handling
│   ├── ShippingErrorDecoder.java             # Shipping error handling
│   └── NotificationErrorDecoder.java         # Notification error handling
│
├── config/
│   └── FeignConfig.java                      # Global Feign configuration
│
├── dto/                                       # Data Transfer Objects
│   ├── ShippingQuoteRequest.java
│   ├── ShippingQuoteResponse.java
│   ├── PaymentVerificationRequest.java
│   ├── PaymentVerificationResponse.java
│   ├── RefundRequest.java
│   ├── RefundResponse.java
│   ├── EmailNotificationRequest.java
│   └── SmsNotificationRequest.java
│
├── interceptor/
│   └── FeignRequestInterceptor.java           # Custom request interceptor
│
└── service/                                   # Business Logic Services
    ├── PaymentFeignService.java               # Payment operations
    ├── ShippingFeignService.java              # Shipping operations
    └── NotificationFeignService.java          # Notification operations
```

## How Each Component Works

### 1. Feign Client (Interface)

```java
@FeignClient(
    name = "payment-gateway",
    url = "${payment.gateway.url:http://localhost:8082}",
    configuration = PaymentFeignConfig.class
)
public interface PaymentGatewayClient {
    @PostMapping("/api/payments/verify")
    PaymentVerificationResponse verifyPayment(@RequestBody PaymentVerificationRequest request);
}
```

**Components**:
- `name`: Unique identifier for the client
- `url`: Base URL of external service (can be configured in properties)
- `configuration`: Custom configuration class for this client

### 2. Configuration Class

```java
@Configuration
public class PaymentFeignConfig {
    @Bean
    public OkHttpClient client() {
        return new OkHttpClient();  // HTTP client
    }

    @Bean
    public Logger.Level feignLoggerLevel() {
        return Logger.Level.FULL;   // Logging level
    }

    @Bean
    public ErrorDecoder errorDecoder() {
        return new PaymentErrorDecoder();  // Custom error handler
    }
}
```

### 3. Error Decoder

```java
@Slf4j
public class PaymentErrorDecoder implements ErrorDecoder {
    @Override
    public Exception decode(String methodKey, Response response) {
        log.error("Payment Service Error: {} - {}", response.status(), response.reason());
        
        return switch (response.status()) {
            case 402 -> new RuntimeException("Payment declined");
            case 401 -> new SecurityException("Authentication failed");
            case 500 -> new RuntimeException("Service unavailable");
            default -> new RuntimeException("Unknown error");
        };
    }
}
```

### 4. Service Layer

```java
@Service
@RequiredArgsConstructor
public class PaymentFeignService {
    private final PaymentGatewayClient paymentGatewayClient;
    
    public PaymentVerificationResponse verifyPayment(PaymentVerificationRequest request) {
        try {
            return paymentGatewayClient.verifyPayment(request);
        } catch (Exception e) {
            log.error("Payment verification failed", e);
            throw new RuntimeException("Verification failed", e);
        }
    }
}
```

### 5. Controller Usage

```java
@RestController
@RequestMapping("/api/feign-demo")
@RequiredArgsConstructor
public class FeignDemoController {
    private final PaymentFeignService paymentFeignService;
    
    @PostMapping("/payments/verify")
    public ResponseEntity<PaymentVerificationResponse> verifyPayment(
            @RequestBody PaymentVerificationRequest request) {
        return ResponseEntity.ok(paymentFeignService.verifyPayment(request));
    }
}
```

## Real-World Integration Examples

### Example 1: Razorpay Payment Gateway Integration

```java
@FeignClient(
    name = "razorpay",
    url = "https://api.razorpay.com",
    configuration = RazorpayFeignConfig.class
)
public interface RazorpayClient {
    
    @PostMapping("/v1/payments/verify_payment_link_id")
    RazorpayVerificationResponse verifyPayment(
        @RequestParam String payment_link_id
    );
    
    @PostMapping("/v1/refunds")
    RazorpayRefundResponse createRefund(
        @RequestParam String payment_id,
        @RequestParam Integer amount
    );
}
```

### Example 2: Shippo Shipping API Integration

```java
@FeignClient(
    name = "shippo",
    url = "https://api.goshippo.com",
    configuration = ShippoFeignConfig.class
)
public interface ShippoClient {
    
    @PostMapping("/shipments")
    ShippoShipmentResponse createShipment(
        @RequestBody ShippoShipmentRequest request
    );
    
    @GetMapping("/shipments/{id}/rates")
    List<ShippoRateResponse> getRates(@PathVariable String id);
}
```

### Example 3: SendGrid Email Integration

```java
@FeignClient(
    name = "sendgrid",
    url = "https://api.sendgrid.com",
    configuration = SendgridFeignConfig.class
)
public interface SendgridClient {
    
    @PostMapping("/v3/mail/send")
    void sendEmail(@RequestBody SendgridEmailRequest request);
}
```

## Configuration Patterns

### Pattern 1: Basic Configuration

```properties
feign.client.config.payment-gateway.url=http://localhost:8082
feign.client.config.payment-gateway.connectTimeout=5000
feign.client.config.payment-gateway.readTimeout=10000
feign.client.config.payment-gateway.loggerLevel=FULL
```

### Pattern 2: With Authentication

```java
@Configuration
public class SecurePaymentFeignConfig {
    
    @Bean
    public RequestInterceptor basicAuthRequestInterceptor() {
        return new BasicAuthRequestInterceptor("username", "password");
    }
}
```

### Pattern 3: With Bearer Token

```java
@Component
public class BearerTokenInterceptor implements RequestInterceptor {
    
    @Value("${feign.auth.token}")
    private String authToken;
    
    @Override
    public void apply(RequestTemplate template) {
        template.header("Authorization", "Bearer " + authToken);
    }
}
```

### Pattern 4: With API Key

```java
@Component
public class ApiKeyInterceptor implements RequestInterceptor {
    
    @Value("${feign.api.key}")
    private String apiKey;
    
    @Override
    public void apply(RequestTemplate template) {
        template.header("X-API-Key", apiKey);
    }
}
```

## Testing Feign Clients

### Unit Test Example

```java
@SpringBootTest
class PaymentFeignServiceTest {
    
    @MockBean
    private PaymentGatewayClient paymentGatewayClient;
    
    @Autowired
    private PaymentFeignService paymentFeignService;
    
    @Test
    void testVerifyPaymentSuccess() {
        PaymentVerificationRequest request = PaymentVerificationRequest.builder()
            .paymentId("pay_123")
            .orderId("order_456")
            .build();
            
        PaymentVerificationResponse mockResponse = PaymentVerificationResponse.builder()
            .paymentId("pay_123")
            .status("captured")
            .amount(BigDecimal.valueOf(999.99))
            .build();
        
        when(paymentGatewayClient.verifyPayment(request))
            .thenReturn(mockResponse);
        
        PaymentVerificationResponse response = paymentFeignService.verifyPayment(request);
        
        assertThat(response.getStatus()).isEqualTo("captured");
        verify(paymentGatewayClient).verifyPayment(request);
    }
}
```

## Common Issues & Solutions

### Issue: 404 Not Found

**Cause**: Incorrect endpoint URL

**Solution**: Verify client URL and endpoint path:

```properties
# Check if URL and path are correct
feign.client.config.payment-gateway.url=https://correct-api-url.com
```

### Issue: Connection Timeout

**Cause**: Service taking too long to respond

**Solution**: Increase timeout values:

```properties
feign.client.config.payment-gateway.connectTimeout=10000
feign.client.config.payment-gateway.readTimeout=20000
```

### Issue: 401 Unauthorized

**Cause**: Missing or invalid authentication

**Solution**: Add interceptor with authentication:

```java
@Bean
public RequestInterceptor authenticationInterceptor() {
    return template -> template.header("Authorization", "Bearer " + getToken());
}
```

### Issue: Content-Type Mismatch

**Cause**: Wrong content type header

**Solution**: Ensure correct content type:

```java
@Bean
public RequestInterceptor contentTypeInterceptor() {
    return template -> template.header("Content-Type", "application/json");
}
```

## Performance Optimization

### 1. Connection Pooling

```java
@Bean
public OkHttpClient okHttpClient() {
    return new OkHttpClient.Builder()
        .connectionPool(new ConnectionPool(10, 60, TimeUnit.SECONDS))
        .build();
}
```

### 2. Request Retry

```properties
feign.client.config.default.retryer=com.example.CustomRetryer
```

### 3. Caching

```java
@Service
public class PaymentFeignService {
    
    @Cacheable(value = "paymentCache", key = "#paymentId")
    public PaymentVerificationResponse getPaymentDetails(String paymentId) {
        return paymentGatewayClient.getPaymentDetails(paymentId);
    }
}
```

## Monitoring & Logging

### Enable Debug Logging

```properties
logging.level.com.quodex._miles.feign=DEBUG
logging.level.feign.Logger=DEBUG
feign.client.config.default.logger-level=FULL
```

### Log Output Example

```
[PaymentGatewayClient#verifyPayment] POST https://api.payment.com/api/payments/verify HTTP/1.1
[PaymentGatewayClient#verifyPayment] Content-Type: application/json
[PaymentGatewayClient#verifyPayment] Authorization: Bearer token123
[PaymentGatewayClient#verifyPayment] {"paymentId":"pay_123"}
---
[PaymentGatewayClient#verifyPayment] HTTP/1.1 200 OK
[PaymentGatewayClient#verifyPayment] Content-Type: application/json
[PaymentGatewayClient#verifyPayment] {"status":"captured","amount":999.99}
```

## Resources

- **Official Documentation**: https://spring.io/projects/spring-cloud-openfeign
- **GitHub**: https://github.com/OpenFeign/feign
- **Spring Cloud Guide**: https://spring.io/guides/gs/consuming-rest

## Next Steps

1. Replace example URLs with actual service URLs
2. Implement circuit breaker pattern
3. Add comprehensive error handling
4. Create integration tests
5. Monitor and log external API calls
6. Implement request/response transformation

