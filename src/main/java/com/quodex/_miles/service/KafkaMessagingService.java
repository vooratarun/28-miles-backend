package com.quodex._miles.service;

import com.quodex._miles.io.KafkaConsumedMessageResponse;
import com.quodex._miles.io.KafkaPublishRequest;
import com.quodex._miles.io.KafkaPublishResponse;

import java.util.List;

public interface KafkaMessagingService {

    KafkaPublishResponse publish(KafkaPublishRequest request);

    List<KafkaConsumedMessageResponse> getRecentMessages();
}
