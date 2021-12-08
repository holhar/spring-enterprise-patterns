package de.holhar.spring.patterns.kafka.consumer;

import de.holhar.spring.patterns.kafka.producer.domain.EventPayload;
import de.holhar.spring.patterns.kafka.producer.domain.EventWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class ConsumerService {

    private static final Logger logger = LoggerFactory.getLogger(ConsumerService.class);

    @KafkaListener(topics = "${app.kafka.topic}", groupId = "${app.kafka.consumer-group-id}")
    public void processKafkaEvent(EventWrapper<EventPayload> event) {
        logger.info("eventSource: {}", event.getEventSource());
        logger.info("eventId: {}", event.getEventId());
        logger.info("eventTime: {}", event.getEventTime());
        logger.info("payload: {}", event.getPayload());
    }
}
