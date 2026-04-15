package com.quodex._miles.feign.service;

import com.quodex._miles.feign.client.ShippingServiceClient;
import com.quodex._miles.feign.dto.ShippingQuoteRequest;
import com.quodex._miles.feign.dto.ShippingQuoteResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Shipping Service using Feign Client
 *
 * This service demonstrates how to use Feign clients for shipping operations.
 * It handles shipping quotes, rates, and tracking through an external shipping provider.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ShippingFeignService {

    private final ShippingServiceClient shippingServiceClient;

    /**
     * Get shipping quote for an order
     */
    public ShippingQuoteResponse getShippingQuote(ShippingQuoteRequest request) {
        log.info("Fetching shipping quote from {} to {}", request.getFromZip(), request.getToZip());
        try {
            return shippingServiceClient.getShippingQuote(request);
        } catch (Exception e) {
            log.error("Failed to get shipping quote", e);
            throw new RuntimeException("Failed to get shipping quote", e);
        }
    }

    /**
     * Get available shipping rates
     */
    public List<ShippingQuoteResponse> getShippingRates(String fromZip, String toZip, Double weight) {
        log.info("Fetching shipping rates for weight: {}", weight);
        try {
            return shippingServiceClient.getShippingRates(fromZip, toZip, weight);
        } catch (Exception e) {
            log.error("Failed to get shipping rates", e);
            throw new RuntimeException("Failed to get shipping rates", e);
        }
    }

    /**
     * Create a shipping label
     */
    public ShippingQuoteResponse createShippingLabel(ShippingQuoteRequest request) {
        log.info("Creating shipping label");
        try {
            return shippingServiceClient.createShippingLabel(request);
        } catch (Exception e) {
            log.error("Failed to create shipping label", e);
            throw new RuntimeException("Failed to create shipping label", e);
        }
    }

    /**
     * Track shipment
     */
    public ShippingQuoteResponse trackShipment(String trackingId) {
        log.info("Tracking shipment: {}", trackingId);
        try {
            return shippingServiceClient.trackShipment(trackingId);
        } catch (Exception e) {
            log.error("Failed to track shipment", e);
            throw new RuntimeException("Failed to track shipment", e);
        }
    }
}

