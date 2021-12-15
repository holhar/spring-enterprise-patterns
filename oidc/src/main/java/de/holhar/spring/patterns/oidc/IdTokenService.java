package de.holhar.spring.patterns.oidc;

import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.util.Optional;

/**
 * Retrieves OIDC id token from current session.
 */
@Service
public class IdTokenService {

    private static final Logger logger = LoggerFactory.getLogger(IdTokenService.class);

    public Optional<String> getToken() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication instanceof OAuth2AuthenticationToken && authentication.getPrincipal() instanceof OidcUser) {
            String tokenValue = ((OidcUser) authentication.getPrincipal()).getIdToken().getTokenValue();
            return Optional.of(tokenValue);
        } else {
            return Optional.empty();
        }
    }

    public Optional<String> getUserId() {
        Optional<String> tokenOptional = getToken();
        if (tokenOptional.isPresent()) {
            try {
                JWTClaimsSet claimsSet = SignedJWT.parse(tokenOptional.get()).getJWTClaimsSet();
                return Optional.of(claimsSet.getSubject());
            } catch (ParseException e) {
                logger.warn("Failed to parse token");
            }
        }
        return Optional.empty();
    }
}
