package com.quodex._miles.feign.client;

import com.quodex._miles.feign.dto.ShippingQuoteRequest;
import com.quodex._miles.feign.dto.ShippingQuoteResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Shipping Service Feign Client
 *
 * This client demonstrates how to call an external shipping service API.
 * Example: Could connect to services like Shippo, EasyPost, or internal shipping APIs.
 *
 * Example URL Configuration in application.properties:
 * feign.client.config.shipping-service.url=https://api.shipping-provider.com
 * feign.client.config.shipping-service.connectTimeout=5000
 * feign.client.config.shipping-service.readTimeout=10000
 */
@FeignClient(
    name = "shipping-service",
    url = "${shipping.service.url:http://localhost:8080}",
    configuration = ShippingFeignConfig.class
)
public interface ShippingServiceClient {

    /**
     * Get shipping quotes for a shipment
     */
    @PostMapping("/api/shipping/quotes")
    ShippingQuoteResponse getShippingQuote(@RequestBody ShippingQuoteRequest request);

    /**
     * Get shipping rates by location
     */
    @GetMapping("/api/shipping/rates")
    List<ShippingQuoteResponse> getShippingRates(
        @RequestParam String fromZip,
        @RequestParam String toZip,
        @RequestParam Double weight
    );

    /**
     * Create a shipping label
     */
    @PostMapping("/api/shipping/labels")
    ShippingQuoteResponse createShippingLabel(@RequestBody ShippingQuoteRequest request);

    /**
     * Track shipment
     */
    @GetMapping("/api/shipping/track/{trackingId}")
    ShippingQuoteResponse trackShipment(@PathVariable String trackingId);
}

