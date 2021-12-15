package de.holhar.spring.patterns.ouath2.client.core;

import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.converter.Converter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.RequestEntity;
import org.springframework.security.oauth2.client.endpoint.OAuth2ClientCredentialsGrantRequest;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

@RequiredArgsConstructor
public class CustomOAuth2ClientCredentialsGrantRequestEntityConverter implements Converter<OAuth2ClientCredentialsGrantRequest, RequestEntity<?>> {

    private final TokenGenerator tokenGenerator;

    @Override
    public RequestEntity<?> convert(OAuth2ClientCredentialsGrantRequest request) {
        String token = tokenGenerator.generateToken(request.getClientRegistration());
        URI uri = UriComponentsBuilder.fromUriString(request.getClientRegistration().getProviderDetails().getTokenUri())
                .queryParams(createQueryParams(request.getClientRegistration(), request.getGrantType().getValue()))
                .build()
                .toUri();
        return RequestEntity
                .post(uri)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .build();
    }

    private MultiValueMap<String, String> createQueryParams(ClientRegistration clientRegistration, String grantType) {
        MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<>();
        queryParams.add(OAuth2ParameterNames.GRANT_TYPE, grantType);
        if (!clientRegistration.getScopes().isEmpty()) {
            queryParams.add(OAuth2ParameterNames.SCOPE, StringUtils.collectionToDelimitedString(clientRegistration.getScopes(), " "));
        }
        return queryParams;
    }
}
