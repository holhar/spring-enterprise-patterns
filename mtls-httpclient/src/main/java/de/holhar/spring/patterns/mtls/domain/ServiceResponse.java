package de.holhar.spring.patterns.mtls.domain;

import org.springframework.http.HttpHeaders;

import java.util.Map;

public class ServiceResponse {

    private final Map<String, String> headers;
    private final byte[] body;

    public ServiceResponse(HttpHeaders headers, byte[] body) {
        this.headers = headers.toSingleValueMap();
        this.body = body;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public byte[] getBody() {
        return body;
    }
}