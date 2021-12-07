package de.holhar.spring.patterns.kafka.producer.web.schema;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

public class EventRequest implements Serializable {

    @NotBlank(message = "useCase is mandatory")
    private String message;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "useCase:'" + message + "'";
    }
}
