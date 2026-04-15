# Feign Client Architecture Diagrams

## 1. Overall System Architecture

```
┌─────────────────────────────────────────────────────────────────┐
│                    28-Miles E-Commerce Backend                  │
├─────────────────────────────────────────────────────────────────┤
│                                                                   │
│  ┌──────────────────────────────────────────────────────────┐  │
│  │             REST Controllers                             │  │
│  ├──────────────────────────────────────────────────────────┤  │
│  │  FeignDemoController (23 endpoints)                      │  │
│  └──────────────────────────────────────────────────────────┘  │
│                          ↓                                       │
│  ┌──────────────────────────────────────────────────────────┐  │
│  │             Service Layer                                │  │
│  ├──────────────────────────────────────────────────────────┤  │
│  │  ┌─────────────────────────────────────────────────────┐ │  │
│  │  │ PaymentFeignService                                │ │  │
│  │  │ ShippingFeignService                               │ │  │
│  │  │ NotificationFeignService                           │ │  │
│  │  └─────────────────────────────────────────────────────┘ │  │
│  └──────────────────────────────────────────────────────────┘  │
│                          ↓                                       │
│  ┌──────────────────────────────────────────────────────────┐  │
│  │          Feign Client Layer (Declarative REST)           │  │
│  ├──────────────────────────────────────────────────────────┤  │
│  │  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐  │  │
│  │  │   Payment    │  │   Shipping   │  │Notification │  │  │
│  │  │   Gateway    │  │   Service    │  │  Service    │  │  │
│  │  │   Client     │  │   Client     │  │   Client    │  │  │
│  │  └──────────────┘  └──────────────┘  └──────────────┘  │  │
│  └──────────────────────────────────────────────────────────┘  │
│                          ↓                                       │
│  ┌──────────────────────────────────────────────────────────┐  │
│  │        Configuration & Interceptors                       │  │
│  ├──────────────────────────────────────────────────────────┤  │
│  │  FeignRequestInterceptor (Add Headers)                   │  │
│  │  ErrorDecoders (Custom Error Handling)                   │  │
│  │  FeignConfigs (Timeouts, Logging, HTTP Client)           │  │
│  └──────────────────────────────────────────────────────────┘  │
│                          ↓                                       │
└─────────────────────────────────────────────────────────────────┘
         ↓                    ↓                    ↓
    ┌─────────┐         ┌─────────┐         ┌─────────┐
    │ Payment │         │Shipping │         │Notif    │
    │ Gateway │         │Provider │         │Service  │
    │ API     │         │ API     │         │ API     │
    └─────────┘         └─────────┘         └─────────┘
    (Razorpay)          (Shippo)           (SendGrid)
    (Stripe)            (EasyPost)         (Twilio)
    (PayPal)            (DHL)              (Firebase)
```

---

## 2. Request Flow Diagram

```
Client Request
    ↓
HTTP POST /api/v1.0/feign-demo/payments/verify
    ↓
FeignDemoController
    ├─ Receives @RequestBody PaymentVerificationRequest
    ├─ Calls paymentFeignService.verifyPayment(request)
    ↓
PaymentFeignService
    ├─ Validates request (optional)
    ├─ Logs request: "Verifying payment for order: {order_id}"
    ├─ Calls paymentGatewayClient.verifyPayment(request)
    ↓
PaymentGatewayClient (Feign Interface)
    ├─ Builds HTTP Request
    ├─ Applies FeignRequestInterceptor
    │  └─ Adds Headers: X-Request-ID, X-Service-Name, Content-Type, etc.
    ├─ Applies PaymentFeignConfig
    │  └─ Sets Timeout, Logging, HTTP Client (OkHttp)
    ↓
HTTP CLIENT (OkHttp)
    ├─ POST https://api.payment-gateway.com/api/payments/verify
    ├─ Headers: Authorization, X-Request-ID, Content-Type: application/json
    ├─ Body: {"paymentId":"pay_123","orderId":"order_456", ...}
    ↓
External Payment Gateway API
    ├─ Processes payment verification
    ├─ Returns HTTP 200 with response JSON
    ↓
HTTP CLIENT receives Response
    ├─ Status: 200 OK
    ├─ Body: {"status":"captured","amount":999.99,...}
    ↓
Feign Client (Deserialization)
    ├─ Converts JSON to PaymentVerificationResponse DTO
    ├─ If error: PaymentErrorDecoder handles it
    ↓
PaymentFeignService
    ├─ Receives PaymentVerificationResponse
    ├─ Logs response
    ├─ Handles any exceptions
    ↓
FeignDemoController
    ├─ Receives response
    ├─ Returns ResponseEntity.ok(response)
    ↓
HTTP Response: 200 OK
    └─ Body: JSON with payment verification details
```

---

## 3. Three Feign Clients in Detail

```
┌────────────────────────────────────────────────────────────┐
│                 PAYMENT GATEWAY CLIENT                     │
├────────────────────────────────────────────────────────────┤
│                                                              │
│ Interface: PaymentGatewayClient                            │
│ Configuration: PaymentFeignConfig                          │
│ Error Decoder: PaymentErrorDecoder                         │
│ Service: PaymentFeignService                               │
│                                                              │
│ Endpoints:                                                  │
│   • verifyPayment(request)                                 │
│   • getPaymentDetails(paymentId)                           │
│   • initiateRefund(request)                                │
│   • getRefundStatus(refundId)                              │
│   • getPaymentStatus(paymentId)                            │
│                                                              │
│ Config: PaymentFeignConfig.java                            │
│   ├─ HTTP Client: OkHttpClient                             │
│   ├─ Logger Level: FULL                                    │
│   ├─ Error Handler: PaymentErrorDecoder                    │
│   ├─ Connect Timeout: 5000ms                               │
│   └─ Read Timeout: 15000ms                                 │
│                                                              │
│ External Services:                                          │
│   ├─ Razorpay (Indian payments)                            │
│   ├─ Stripe (Global payments)                              │
│   ├─ PayPal (Digital payments)                             │
│   └─ Square (POS/Payments)                                 │
│                                                              │
└────────────────────────────────────────────────────────────┘

┌────────────────────────────────────────────────────────────┐
│                 SHIPPING SERVICE CLIENT                     │
├────────────────────────────────────────────────────────────┤
│                                                              │
│ Interface: ShippingServiceClient                           │
│ Configuration: ShippingFeignConfig                         │
│ Error Decoder: ShippingErrorDecoder                        │
│ Service: ShippingFeignService                              │
│                                                              │
│ Endpoints:                                                  │
│   • getShippingQuote(request)                              │
│   • getShippingRates(fromZip, toZip, weight)               │
│   • createShippingLabel(request)                           │
│   • trackShipment(trackingId)                              │
│                                                              │
│ Config: ShippingFeignConfig.java                           │
│   ├─ HTTP Client: OkHttpClient                             │
│   ├─ Logger Level: FULL                                    │
│   ├─ Error Handler: ShippingErrorDecoder                   │
│   ├─ Connect Timeout: 5000ms                               │
│   └─ Read Timeout: 10000ms                                 │
│                                                              │
│ External Services:                                          │
│   ├─ Shippo (Multi-carrier)                                │
│   ├─ EasyPost (Label printing)                             │
│   ├─ DHL (International)                                   │
│   └─ FedEx/UPS APIs                                        │
│                                                              │
└────────────────────────────────────────────────────────────┘

┌────────────────────────────────────────────────────────────┐
│              NOTIFICATION SERVICE CLIENT                    │
├────────────────────────────────────────────────────────────┤
│                                                              │
│ Interface: NotificationServiceClient                       │
│ Configuration: NotificationFeignConfig                     │
│ Error Decoder: NotificationErrorDecoder                    │
│ Service: NotificationFeignService                          │
│                                                              │
│ Endpoints:                                                  │
│   • sendEmail(request)                                     │
│   • sendSms(request)                                       │
│   • sendPushNotification(request)                          │
│   • sendBulkEmail(request)                                 │
│                                                              │
│ Config: NotificationFeignConfig.java                       │
│   ├─ HTTP Client: OkHttpClient                             │
│   ├─ Logger Level: BASIC                                   │
│   ├─ Error Handler: NotificationErrorDecoder               │
│   ├─ Connect Timeout: 3000ms                               │
│   └─ Read Timeout: 5000ms                                  │
│                                                              │
│ External Services:                                          │
│   ├─ SendGrid (Email)                                      │
│   ├─ Twilio (SMS)                                          │
│   ├─ Firebase (Push)                                       │
│   └─ AWS SES/SNS                                           │
│                                                              │
└────────────────────────────────────────────────────────────┘
```

---

## 4. Data Flow for Payment Verification

```
┌─────────────────────────────────────────────────────────────┐
│                   REQUEST FLOW                              │
└─────────────────────────────────────────────────────────────┘

1. HTTP Request arrives
   │
   ├─ POST /api/v1.0/feign-demo/payments/verify
   ├─ Body: { "paymentId": "pay_123", "orderId": "order_456" }
   │
   ↓

2. FeignDemoController receives request
   │
   ├─ @PostMapping("/payments/verify")
   ├─ Receives: PaymentVerificationRequest
   ├─ Injects: PaymentFeignService
   │
   ↓

3. PaymentFeignService.verifyPayment() called
   │
   ├─ Logs: "Verifying payment for order: order_456"
   ├─ Calls: paymentGatewayClient.verifyPayment(request)
   │
   ↓

4. PaymentGatewayClient (Feign Interface)
   │
   ├─ Spring creates dynamic proxy
   ├─ Builds HTTP request to configured URL
   ├─ Applies RequestInterceptor (FeignRequestInterceptor)
   │  └─ Adds headers: X-Request-ID, X-Service-Name, etc.
   │
   ↓

5. PaymentFeignConfig applies
   │
   ├─ HTTP Client: OkHttpClient
   ├─ Logger Level: FULL (logs full request/response)
   ├─ Timeout Settings: 5000ms connect, 15000ms read
   │
   ↓

6. HTTP Request sent
   │
   ├─ POST https://api.payment-gateway.com/api/payments/verify
   ├─ Headers: [Authorization, X-Request-ID, Content-Type, Accept]
   ├─ Body (JSON): { "paymentId": "pay_123", ... }
   │
   ↓

7. External Service processes
   │
   ├─ Validates payment
   ├─ Returns HTTP 200 OK with response
   │
   ↓

8. Response deserialization
   │
   ├─ Response JSON converted to PaymentVerificationResponse DTO
   ├─ Status fields validated
   ├─ If error (4xx/5xx): PaymentErrorDecoder handles it
   │
   ↓

9. Return to Service
   │
   ├─ PaymentFeignService receives response
   ├─ Logs successful response
   ├─ Returns to Controller
   │
   ↓

10. Return to Controller
    │
    ├─ Wraps in ResponseEntity.ok(response)
    ├─ Returns HTTP 200 with JSON response
    │
    ↓

11. HTTP Response sent to client
    │
    ├─ Status: 200 OK
    ├─ Body: { "status": "captured", "amount": 999.99, ... }

```

---

## 5. Error Handling Flow

```
┌──────────────────────────────────────────────────────────────┐
│              ERROR HANDLING FLOW                             │
└──────────────────────────────────────────────────────────────┘

External API Response
    │
    ├─ HTTP Status: 4xx or 5xx
    ├─ Error Body: { "error": "Payment declined", ... }
    │
    ↓

Feign Client intercepts error response
    │
    ├─ Response Status received
    ├─ Calls ErrorDecoder.decode(methodKey, response)
    │
    ↓

PaymentErrorDecoder (switch statement)
    │
    ├─ 400 → IllegalArgumentException("Invalid payment request")
    ├─ 401 → SecurityException("Payment gateway authentication failed")
    ├─ 402 → RuntimeException("Payment declined")
    ├─ 404 → RuntimeException("Payment record not found")
    ├─ 408 → RuntimeException("Payment timeout")
    ├─ 429 → RuntimeException("Payment service rate limited")
    ├─ 500/502/503 → RuntimeException("Payment service temporarily unavailable")
    ├─ default → RuntimeException("Payment service error: " + response.reason())
    │
    ↓

Exception thrown to caller
    │
    ├─ PaymentFeignService catches exception
    ├─ Logs error: "Failed to verify payment"
    ├─ Throws: RuntimeException("Verification failed")
    │
    ↓

FeignDemoController catches exception
    │
    ├─ Returns ResponseEntity.status(HttpStatus.BAD_REQUEST).build()
    │
    ↓

HTTP Response to Client
    │
    ├─ Status: 400 Bad Request (or appropriate status)
    ├─ Error handling complete
```

---

## 6. Configuration Layers

```
┌──────────────────────────────────────────────────────────────┐
│           FEIGN CONFIGURATION LAYERS                         │
└──────────────────────────────────────────────────────────────┘

┌─ application.properties (Environment Config)
│  ├─ feign.client.config.payment-gateway.url=...
│  ├─ feign.client.config.payment-gateway.connectTimeout=5000
│  ├─ feign.client.config.payment-gateway.readTimeout=15000
│  └─ logging.level.com.quodex._miles.feign.client=DEBUG
│
├─ Application.java (Global Config)
│  └─ @EnableFeignClients(basePackages = "com.quodex._miles.feign.client")
│
├─ PaymentFeignConfig.java (Service-specific Config)
│  ├─ OkHttpClient bean
│  ├─ Logger.Level.FULL bean
│  └─ ErrorDecoder bean
│
├─ FeignRequestInterceptor.java (Global Request Interceptor)
│  └─ Adds headers to all requests
│
└─ PaymentGatewayClient.java (Feign Interface)
   └─ @FeignClient(name="payment-gateway", url="${payment.gateway.url}")
```

---

## 7. DTO Mapping

```
┌──────────────────────────────────────────────────────────────┐
│           REQUEST/RESPONSE DTO MAPPING                       │
└──────────────────────────────────────────────────────────────┘

Payment Verification Flow:

Client JSON
    ↓
@RequestBody PaymentVerificationRequest (DTO)
    ├─ paymentId: String
    ├─ signature: String
    ├─ orderId: String
    ├─ amount: String
    └─ currency: String
    ↓
PaymentGatewayClient (Feign)
    ↓
HTTP JSON Body
    ↓
External Payment API
    ↓
HTTP JSON Response
    ↓
PaymentVerificationResponse (DTO)
    ├─ paymentId: String
    ├─ status: String
    ├─ amount: BigDecimal
    ├─ currency: String
    ├─ method: String
    ├─ createdAt: LocalDateTime
    ├─ orderId: String
    ├─ authenticated: Boolean
    └─ failureReason: String
    ↓
@ResponseBody returns ResponseEntity<PaymentVerificationResponse>
    ↓
Client JSON Response

```

---

## 8. File Organization

```
com.quodex._miles/
│
├── feign/                          (New Package)
│   │
│   ├── client/                     (Feign Interfaces & Config)
│   │   ├── PaymentGatewayClient.java
│   │   ├── ShippingServiceClient.java
│   │   ├── NotificationServiceClient.java
│   │   ├── PaymentFeignConfig.java
│   │   ├── ShippingFeignConfig.java
│   │   ├── NotificationFeignConfig.java
│   │   ├── PaymentErrorDecoder.java
│   │   ├── ShippingErrorDecoder.java
│   │   └── NotificationErrorDecoder.java
│   │
│   ├── config/                     (Global Config)
│   │   └── FeignConfig.java
│   │
│   ├── dto/                        (Request/Response Objects)
│   │   ├── ShippingQuoteRequest.java
│   │   ├── ShippingQuoteResponse.java
│   │   ├── PaymentVerificationRequest.java
│   │   ├── PaymentVerificationResponse.java
│   │   ├── RefundRequest.java
│   │   ├── RefundResponse.java
│   │   ├── EmailNotificationRequest.java
│   │   └── SmsNotificationRequest.java
│   │
│   ├── interceptor/                (Request Interceptor)
│   │   └── FeignRequestInterceptor.java
│   │
│   └── service/                    (Business Logic)
│       ├── PaymentFeignService.java
│       ├── ShippingFeignService.java
│       └── NotificationFeignService.java
│
└── controller/                     (REST Endpoints)
    └── FeignDemoController.java
```

---

**Visual diagrams created for better understanding of Feign architecture and data flows.**

