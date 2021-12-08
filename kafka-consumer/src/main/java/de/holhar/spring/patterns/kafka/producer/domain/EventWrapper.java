package de.holhar.spring.patterns.kafka.producer.domain;

import com.fasterxml.jackson.annotation.JsonTypeInfo;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

public class EventWrapper<T extends Serializable> implements Serializable {
    private static final long serialVersionUID = 1L;

    private String eventSource;
    private UUID eventId;
    private LocalDateTime eventTime;

    @JsonTypeInfo(use= JsonTypeInfo.Id.CLASS, include= JsonTypeInfo.As.PROPERTY, property="class")
    private T payload;

    public EventWrapper() {
    }

    public EventWrapper(String groupId, T payload) {
        this(groupId, UUID.randomUUID(), LocalDateTime.now(), payload);
    }

    private EventWrapper(String eventSource, UUID eventId, LocalDateTime eventTime, T payload) {
        this.eventSource = eventSource;
        this.eventId = eventId;
        this.eventTime = eventTime;
        this.payload = payload;
    }

    public String getEventSource() {
        return eventSource;
    }

    public void setEventSource(String eventSource) {
        this.eventSource = eventSource;
    }

    public UUID getEventId() {
        return eventId;
    }

    public void setEventId(UUID eventId) {
        this.eventId = eventId;
    }

    public LocalDateTime getEventTime() {
        return eventTime;
    }

    public void setEventTime(LocalDateTime eventTime) {
        this.eventTime = eventTime;
    }

    public T getPayload() {
        return this.payload;
    }

    public void setPayload(T payload) {
        this.payload = payload;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        } else if (!(o instanceof EventWrapper)) {
            return false;
        } else {
            EventWrapper that = (EventWrapper) o;
            return this.eventSource.equals(that.eventSource) && this.eventId.equals(that.eventId);
        }
    }

    public int hashCode() {
        int result = this.eventSource.hashCode();
        result = 31 * result + this.eventId.hashCode();
        return result;
    }
}
