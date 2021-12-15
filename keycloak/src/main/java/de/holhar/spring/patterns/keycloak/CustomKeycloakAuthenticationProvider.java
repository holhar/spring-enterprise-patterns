package de.holhar.spring.patterns.keycloak;

import org.keycloak.adapters.springsecurity.account.KeycloakRole;
import org.keycloak.adapters.springsecurity.authentication.KeycloakAuthenticationProvider;
import org.keycloak.adapters.springsecurity.token.KeycloakAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class CustomKeycloakAuthenticationProvider extends KeycloakAuthenticationProvider {

    private final String resourceKey;
    private GrantedAuthoritiesMapper grantedAuthoritiesMapper;

    public CustomKeycloakAuthenticationProvider(String resourceKey) {
        this.resourceKey = resourceKey;
    }

    @Override
    public void setGrantedAuthoritiesMapper(GrantedAuthoritiesMapper grantedAuthoritiesMapper) {
        this.grantedAuthoritiesMapper = grantedAuthoritiesMapper;
    }

    @Override
    public Authentication authenticate(Authentication authentication) {
        KeycloakAuthenticationToken token = (KeycloakAuthenticationToken) authentication;
        List<GrantedAuthority> grantedAuthorities = new ArrayList<>();

        for (String role : token.getAccount().getRoles()) {
            grantedAuthorities.add(new KeycloakRole(role));
        }

        for (String role : token
                .getAccount()
                .getKeycloakSecurityContext()
                .getToken()
                .getResourceAccess(resourceKey)
                .getRoles()) {
            grantedAuthorities.add(new KeycloakRole(role));
        }
        return new KeycloakAuthenticationToken(token.getAccount(), token.isInteractive(), mapGrantedAuthorities(grantedAuthorities));
    }

    private Collection<? extends GrantedAuthority> mapGrantedAuthorities(
            Collection<? extends GrantedAuthority> authorities) {
        return grantedAuthoritiesMapper != null
                ? grantedAuthoritiesMapper.mapAuthorities(authorities)
                : authorities;
    }

    @Override
    public boolean supports(Class<?> aClass) {
        return KeycloakAuthenticationToken.class.isAssignableFrom(aClass);
    }
}