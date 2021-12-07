package de.holhar.spring.patterns.kafka.producer.web;

import de.holhar.spring.patterns.kafka.producer.web.schema.EventRequest;
import de.holhar.spring.patterns.kafka.producer.web.schema.EventResponse;
import de.holhar.spring.patterns.kafka.producer.KafkaMessagingService;
import de.holhar.spring.patterns.kafka.producer.domain.EventPayload;
import io.micrometer.core.instrument.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/events")
public class NotificationController {
    private static final Logger LOGGER = LoggerFactory.getLogger(NotificationController.class);

    private final KafkaMessagingService kafkaMessagingService;

    public NotificationController(KafkaMessagingService kafkaMessagingService) {
        this.kafkaMessagingService = kafkaMessagingService;
    }

    @PostMapping("/notify")
    public ResponseEntity<EventResponse> sendUserNotification(@Valid @RequestBody EventRequest eventRequest) {

        String message = eventRequest.getMessage();
        if (StringUtils.isBlank(message)) {
            throw new IllegalArgumentException("userId or message is blank");
        }
        EventPayload eventPayload = new EventPayload();
        eventPayload.setMessage(message);

        try {
            LOGGER.debug("received -> {}", eventPayload);
            kafkaMessagingService.send(eventPayload);
            return ResponseEntity.ok(new EventResponse(EventResponse.Status.OK));
        } catch (Exception e) {
            LOGGER.error("Writing to kafka failed", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new EventResponse(EventResponse.Status.ERROR, e.getMessage()));
        }
    }
}
