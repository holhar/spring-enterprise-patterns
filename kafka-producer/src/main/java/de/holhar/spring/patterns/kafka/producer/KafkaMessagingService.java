package de.holhar.spring.patterns.kafka.producer;

import de.holhar.spring.patterns.kafka.producer.config.KafkaConfiguration;
import de.holhar.spring.patterns.kafka.producer.domain.EventPayload;
import de.holhar.spring.patterns.kafka.producer.domain.EventWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class KafkaMessagingService {
    private static final Logger LOGGER = LoggerFactory.getLogger(KafkaMessagingService.class);

    private final KafkaTemplate<String, EventWrapper<EventPayload>> kafkaTemplate;
    private final KafkaConfiguration kafkaConfig;

    public KafkaMessagingService(KafkaTemplate<String, EventWrapper<EventPayload>> kafkaEmailTemplate, KafkaConfiguration kafkaConfig) {
        this.kafkaTemplate = kafkaEmailTemplate;
        this.kafkaConfig = kafkaConfig;
    }

    public void send(EventPayload eventPayload) {
        EventWrapper<EventPayload> event = new EventWrapper<>(kafkaConfig.getConsumerGroupId(), eventPayload);
        kafkaTemplate.send(kafkaConfig.getTopic(), event);
        LOGGER.debug("sent event[{}][{}] -> {}", event.getEventId(), event.getEventTime(), event.getPayload());
    }
}