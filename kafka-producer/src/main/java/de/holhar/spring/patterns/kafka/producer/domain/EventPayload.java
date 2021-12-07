package de.holhar.spring.patterns.kafka.producer.domain;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

public class EventPayload implements Serializable {
    private static final long serialVersionUID = 1L;

    @NotBlank(message = "message is mandatory")
    private String message;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "message:'" + message + "'";
    }
}
