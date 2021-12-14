package de.holhar.spring.patterns.mtls.domain;

public class ServiceException extends RuntimeException {

    private final int statusCode;
    private final String reasonPhrase;
    private final String body;

    public ServiceException(int statusCode, String reasonPhrase, String body) {
        this.statusCode = statusCode;
        this.reasonPhrase = reasonPhrase;
        this.body = body;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public String getReasonPhrase() {
        return reasonPhrase;
    }

    public String getBody() {
        return body;
    }
}