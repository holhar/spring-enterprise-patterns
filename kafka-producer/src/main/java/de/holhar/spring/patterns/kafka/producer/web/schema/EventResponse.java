package de.holhar.spring.patterns.kafka.producer.web.schema;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.LinkedList;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class EventResponse {
    public enum Status {
        OK,
        ERROR,
        BAD_REQUEST
    }

    private Status status;
    private List<String> errors;

    public EventResponse(Status status, List<String> errors) {
        this.status = status;
        this.errors = errors;
    }

    public EventResponse(Status status, String error) {
        this.status = status;
        List<String> errorList = new LinkedList<>();
        errorList.add(error);
        this.errors = errorList;
    }

    public EventResponse(Status status) {
        this.status = status;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public List<String> getErrors() {
        return errors;
    }

    public void setErrors(List<String> errors) {
        this.errors = errors;
    }
}