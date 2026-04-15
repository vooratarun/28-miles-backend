package com.quodex._miles.controller;

import com.quodex._miles.io.KafkaConsumedMessageResponse;
import com.quodex._miles.io.KafkaPublishRequest;
import com.quodex._miles.io.KafkaPublishResponse;
import com.quodex._miles.service.KafkaMessagingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/kafka")
@RequiredArgsConstructor
public class KafkaController {

    private final KafkaMessagingService kafkaMessagingService;

    @PostMapping("/publish")
    public ResponseEntity<KafkaPublishResponse> publish(@RequestBody KafkaPublishRequest request) {
        KafkaPublishResponse response = kafkaMessagingService.publish(request);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(response);
    }

    @GetMapping("/messages")
    public ResponseEntity<List<KafkaConsumedMessageResponse>> getRecentMessages() {
        return ResponseEntity.ok(kafkaMessagingService.getRecentMessages());
    }
}
