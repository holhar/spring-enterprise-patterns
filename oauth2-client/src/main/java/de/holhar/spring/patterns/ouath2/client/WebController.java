package de.holhar.spring.patterns.ouath2.client;

import org.springframework.security.oauth2.client.AuthorizedClientServiceOAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.OAuth2AuthorizeRequest;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class WebController {

    private final AuthorizedClientServiceOAuth2AuthorizedClientManager clientManager;

    public WebController(AuthorizedClientServiceOAuth2AuthorizedClientManager clientManager) {
        this.clientManager = clientManager;
    }

    @GetMapping("/token")
    public String getToken() {
        OAuth2AuthorizeRequest request = OAuth2AuthorizeRequest
                .withClientRegistrationId("ourclient")
                .principal("holhar")
                .build();
        OAuth2AuthorizedClient authorizedClient = clientManager.authorize(request);
        return authorizedClient.getAccessToken().getTokenValue();
    }
}
