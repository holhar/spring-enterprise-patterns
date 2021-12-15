package de.holhar.spring.patterns.ouath2.client.core;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.ClientCredentialsOAuth2AuthorizedClientProvider;
import org.springframework.security.oauth2.client.OAuth2AuthorizationContext;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientProvider;
import org.springframework.security.oauth2.client.endpoint.DefaultClientCredentialsTokenResponseClient;
import org.springframework.security.oauth2.client.endpoint.OAuth2ClientCredentialsGrantRequest;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.endpoint.OAuth2AccessTokenResponse;

import java.time.Clock;
import java.time.Duration;

/**
 * See {@link ClientCredentialsOAuth2AuthorizedClientProvider} for a comparison.
 */
@Slf4j
@RequiredArgsConstructor
public class CustomOAuth2AuthorizedClientProvider implements OAuth2AuthorizedClientProvider {

    private final TokenGenerator tokenGenerator;
    private final Clock clock = Clock.systemUTC();
    private Duration clockSkew = Duration.ofMinutes(1);

    @Override
    public OAuth2AuthorizedClient authorize(OAuth2AuthorizationContext context) {
        String reason = "not present";
        if (context.getAuthorizedClient() != null) {
            OAuth2AccessToken accessToken = context.getAuthorizedClient().getAccessToken();
            if (accessToken != null) {
                if (isExpired(accessToken)) {
                    reason = "expired";
                } else {
                    // null signals: nothing to do. Reuse the authorized client in the context.
                    return null;
                }
            } else {
                // null signals: nothing to do. Reuse the authorized client in the context.
                return null;
            }
        }
        ClientRegistration clientRegistration = context.getClientRegistration();
        log.info("requesting new access token for client registered as \"{}\". Reason: {}.", clientRegistration.getRegistrationId(), reason);

        OAuth2ClientCredentialsGrantRequest grantRequest = new OAuth2ClientCredentialsGrantRequest(clientRegistration);
        DefaultClientCredentialsTokenResponseClient grantResponseClient = new DefaultClientCredentialsTokenResponseClient();
        grantResponseClient.setRequestEntityConverter(new CustomOAuth2ClientCredentialsGrantRequestEntityConverter(tokenGenerator));
        OAuth2AccessTokenResponse tokenResponse = grantResponseClient.getTokenResponse(grantRequest);
        OAuth2AccessToken accessToken = tokenResponse.getAccessToken();
        return new OAuth2AuthorizedClient(clientRegistration, "client", accessToken);
    }

    public void setClockSkew(Duration clockSkew) {
        this.clockSkew = clockSkew;
    }

    private boolean isExpired(OAuth2AccessToken accessToken) {
        return this.clock.instant().isAfter(accessToken.getExpiresAt().minus(this.clockSkew));
    }
}
