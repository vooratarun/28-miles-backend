package com.quodex._miles.service.Impl;

import com.quodex._miles.config.KafkaProperties;
import com.quodex._miles.io.KafkaConsumedMessageResponse;
import com.quodex._miles.io.KafkaPublishRequest;
import com.quodex._miles.io.KafkaPublishResponse;
import com.quodex._miles.service.KafkaMessagingService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Service
@RequiredArgsConstructor
public class KafkaMessagingServiceImpl implements KafkaMessagingService {

    private static final Logger log = LoggerFactory.getLogger(KafkaMessagingServiceImpl.class);
    private static final int MAX_STORED_MESSAGES = 20;

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final KafkaProperties kafkaProperties;
    private final List<KafkaConsumedMessageResponse> consumedMessages = new CopyOnWriteArrayList<>();

    @Override
    public KafkaPublishResponse publish(KafkaPublishRequest request) {
        if (!kafkaProperties.isEnabled()) {
            throw new IllegalStateException("Kafka is not enabled. Set app.kafka.enabled=true");
        }

        if (request == null || !StringUtils.hasText(request.getMessage())) {
            throw new IllegalArgumentException("Kafka message is required");
        }

        String topic = StringUtils.hasText(request.getTopic()) ? request.getTopic() : kafkaProperties.getTopic();

        if (StringUtils.hasText(request.getKey())) {
            kafkaTemplate.send(topic, request.getKey(), request.getMessage());
        } else {
            kafkaTemplate.send(topic, request.getMessage());
        }

        KafkaPublishResponse response = new KafkaPublishResponse();
        response.setTopic(topic);
        response.setKey(request.getKey());
        response.setMessage(request.getMessage());
        response.setStatus("SENT");
        return response;
    }

    @Override
    public List<KafkaConsumedMessageResponse> getRecentMessages() {
        return new ArrayList<>(consumedMessages);
    }

    @KafkaListener(
            topics = "#{@kafkaProperties.topic}",
            groupId = "#{@kafkaProperties.groupId}",
            autoStartup = "#{@kafkaProperties.enabled}"
    )
    public void consume(
            String message,
            @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
            @Header(value = KafkaHeaders.RECEIVED_KEY, required = false) String key,
            @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
            @Header(KafkaHeaders.OFFSET) long offset
    ) {
        KafkaConsumedMessageResponse response = new KafkaConsumedMessageResponse();
        response.setTopic(topic);
        response.setKey(key);
        response.setMessage(message);
        response.setPartition(partition);
        response.setOffset(offset);

        consumedMessages.add(0, response);
        while (consumedMessages.size() > MAX_STORED_MESSAGES) {
            consumedMessages.remove(consumedMessages.size() - 1);
        }

        log.info("Consumed Kafka message from topic={} partition={} offset={}", topic, partition, offset);
    }
}
