package de.holhar.spring.patterns.ouath2.client.core;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JOSEObjectType;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.security.oauth2.client.registration.ClientRegistration;

import java.io.IOException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.UUID;

/**
 * TokenGenerator generates signed JWTs for authentication against the corresponding OAuth 2 provider server.
 */
@Slf4j
public class TokenGenerator {

    private final Resource privateKeyResource;
    private final Clock clock = Clock.systemUTC();
    private final Duration expireDuration = Duration.ofSeconds(60);

    public TokenGenerator(Resource privateKeyResource) {
        this.privateKeyResource = privateKeyResource;
    }

    public String generateToken(ClientRegistration clientRegistration) {
        try {
            PrivateKey privateKey = getPrivateKey();
            JWTClaimsSet jwtClaimsSet = buildClaims(clientRegistration);
            return signClaims(privateKey, jwtClaimsSet, clientRegistration.getClientId());
        } catch (Exception e) {
            throw new IllegalStateException("Failed to generate JWT for OAuth2 client authentication");
        }
    }

    private PrivateKey getPrivateKey() throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
        byte[] bytes = privateKeyResource.getInputStream().readAllBytes();
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(bytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return keyFactory.generatePrivate(keySpec);
    }

    private JWTClaimsSet buildClaims(ClientRegistration clientRegistration) {
        Instant iat = clock.instant();
        Instant exp = iat.plus(expireDuration);

        return new JWTClaimsSet.Builder()
                .subject(clientRegistration.getClientId())
                .audience(clientRegistration.getProviderDetails().getTokenUri()) // Should be validated for a valid URI, but I'm too lazy right now
                .issuer(clientRegistration.getClientId())
                .issueTime(new Date(iat.toEpochMilli()))
                .expirationTime(new Date(exp.toEpochMilli()))
                .jwtID(UUID.randomUUID().toString())
                .build();
    }

    private String signClaims(PrivateKey privateKey, JWTClaimsSet jwtClaimsSet, String clientId) {
        JWSSigner signer = new RSASSASigner(privateKey);
        SignedJWT signedJWT = new SignedJWT(
                new JWSHeader.Builder(JWSAlgorithm.RS256).keyID(clientId).type(JOSEObjectType.JWT).build(),
                jwtClaimsSet
        );
        try {
            signedJWT.sign(signer);
        } catch (JOSEException e) {
            throw new IllegalStateException("Failed to sigh JWT for OAuth2 client authentication");
        }
        return signedJWT.serialize();
    }
}
