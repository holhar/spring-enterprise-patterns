package de.holhar.spring.patterns.mtls;

import de.holhar.spring.patterns.mtls.domain.ServiceException;
import de.holhar.spring.patterns.mtls.domain.ServiceResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientException;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.nio.charset.StandardCharsets;

@Service
public class HttpClientService {
    
    private static final Logger logger = LoggerFactory.getLogger(HttpClientService.class);

    private static final String baseUrl = "http://localhost:8888/some/service/uri";

    private final RestTemplate mtlsRestTemplate;
    private final WebClient mtlsWebClient;

    public HttpClientService(RestTemplate mtlsRestTemplate, WebClient mtlsWebClient) {
        this.mtlsRestTemplate = mtlsRestTemplate;
        this.mtlsWebClient = mtlsWebClient;
    }

    public String restTemplateCall(String jsonString) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> entity = new HttpEntity<>(jsonString, headers);
        ResponseEntity<byte[]> response = mtlsRestTemplate.exchange(baseUrl, HttpMethod.POST, entity, byte[].class);

        if (response.getStatusCode().isError()) {
            // Handle failing request
            return "";
        }

        return response.getBody() == null ? "" : new String(response.getBody());
    }

    // TODO: Write tests
    public String webClientCall(String jsonString) {
        ServiceResponse response;
        try {
            response = mtlsWebClient.method(HttpMethod.POST)
                    .uri(baseUrl)
                    .headers(h -> h.setContentType(MediaType.APPLICATION_JSON))
                    .body(BodyInserters.fromValue(jsonString))
                    .retrieve()
                    .toEntity(byte[].class)
                    .map(entity -> new ServiceResponse(entity.getHeaders(), entity.getBody()))
                    .onErrorMap(WebClientException.class, this::handleHttpClientException)
                    .block();
        } catch (ServiceException e) {
            // Handle failing request
            return "";
        }
        byte[] responseBody = (response == null || response.getBody() == null) ? new byte[0] : response.getBody();
        return responseBody.length > 0 ? new String(responseBody) : "";
    }

    private Throwable handleHttpClientException(Throwable t) {
        if (!(t instanceof WebClientResponseException)) {
            logger.warn("Got an unexpected error: {}, will rethrow it", t.getClass().getSimpleName());
            return t;
        }
        WebClientResponseException e = (WebClientResponseException) t;
        return new ServiceException(e.getRawStatusCode(), e.getStatusCode().getReasonPhrase(), e.getResponseBodyAsString(StandardCharsets.UTF_8));
    }
}
