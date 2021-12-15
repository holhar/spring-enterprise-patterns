package de.holhar.spring.patterns.ouath2.client;

import de.holhar.spring.patterns.ouath2.client.core.CustomOAuth2AuthorizedClientProvider;
import de.holhar.spring.patterns.ouath2.client.core.TokenGenerator;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.security.oauth2.client.AuthorizedClientServiceOAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientProvider;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientProviderBuilder;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;

import java.time.Duration;

@Configuration
@ConfigurationProperties("app")
public class OAuthClientConfig {

    @Setter
    @Getter
    private Resource privateKeyResource;

    @Bean
    public OAuth2AuthorizedClientProvider authorizedClientProvider() {
        TokenGenerator tokenGenerator = new TokenGenerator(privateKeyResource);
        CustomOAuth2AuthorizedClientProvider clientProvider = new CustomOAuth2AuthorizedClientProvider(tokenGenerator);
        clientProvider.setClockSkew(Duration.ofMinutes(2));
        return clientProvider;
    }

    @Bean
    public AuthorizedClientServiceOAuth2AuthorizedClientManager oAuth2AuthorizedClientManager(
            ClientRegistrationRepository clientRegistrationRepository,
            OAuth2AuthorizedClientService oAuth2AuthorizedClientService,
            OAuth2AuthorizedClientProvider authorizedClientProvider
    ) {
        OAuth2AuthorizedClientProvider oAuth2AuthorizedClientProvider = OAuth2AuthorizedClientProviderBuilder.builder()
                .provider(authorizedClientProvider)
                .build();

        AuthorizedClientServiceOAuth2AuthorizedClientManager oAuth2AuthorizedClientManager = new AuthorizedClientServiceOAuth2AuthorizedClientManager(
                clientRegistrationRepository, oAuth2AuthorizedClientService
        );

        oAuth2AuthorizedClientManager.setAuthorizedClientProvider(oAuth2AuthorizedClientProvider);
        return oAuth2AuthorizedClientManager;
    }
}
