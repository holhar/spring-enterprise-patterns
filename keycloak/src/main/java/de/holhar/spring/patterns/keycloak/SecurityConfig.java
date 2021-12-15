package de.holhar.spring.patterns.keycloak;

import org.keycloak.adapters.KeycloakConfigResolver;
import org.keycloak.adapters.springboot.KeycloakSpringBootConfigResolver;
import org.keycloak.adapters.springsecurity.KeycloakConfiguration;
import org.keycloak.adapters.springsecurity.authentication.KeycloakAuthenticationEntryPoint;
import org.keycloak.adapters.springsecurity.authentication.KeycloakLogoutHandler;
import org.keycloak.adapters.springsecurity.config.KeycloakWebSecurityConfigurerAdapter;
import org.keycloak.adapters.springsecurity.filter.AdapterStateCookieRequestMatcher;
import org.keycloak.adapters.springsecurity.filter.KeycloakAuthenticationProcessingFilter;
import org.keycloak.adapters.springsecurity.filter.KeycloakPreAuthActionsFilter;
import org.keycloak.adapters.springsecurity.filter.QueryParamPresenceRequestMatcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.authority.mapping.SimpleAuthorityMapper;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.web.authentication.session.RegisterSessionAuthenticationStrategy;
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.OrRequestMatcher;
import org.springframework.security.web.util.matcher.RequestHeaderRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

@KeycloakConfiguration
public class SecurityConfig extends KeycloakWebSecurityConfigurerAdapter {

    @Value("${app.keycloak.loginUrl}")
    private String keycloakLoginUrl;

    @Value("${app.keycloak.logoutUrl}")
    private String keycloakLogoutUrl;

    /**
     * Sets Keycloaks config resolver to use Springs application.properties instead of keycloak.json (which is default).
     */
    @Bean
    public KeycloakConfigResolver keycloakConfigResolver() {
        return new KeycloakSpringBootConfigResolver();
    }

    /**
     * Registers the KeycloakAuthenticationProvider with the authentication manager.
     */
    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth,
                                @Value("${keycloak-resource-key}") String resourceKey) {
        var keycloakAuthenticationProvider = new CustomKeycloakAuthenticationProvider(resourceKey);
        keycloakAuthenticationProvider.setGrantedAuthoritiesMapper(new SimpleAuthorityMapper());
        auth.authenticationProvider(keycloakAuthenticationProvider());
    }

    @Bean
    @Override
    protected KeycloakAuthenticationProcessingFilter keycloakAuthenticationProcessingFilter() throws Exception {
        RequestMatcher requestMatcher = new OrRequestMatcher(
                new AntPathRequestMatcher(keycloakLoginUrl),
                new RequestHeaderRequestMatcher("Authorization"),
                new QueryParamPresenceRequestMatcher("access_token"),
                new AdapterStateCookieRequestMatcher());

        return new KeycloakAuthenticationProcessingFilter(authenticationManagerBean(), requestMatcher);
    }

    /**
     * Defines the session authentication strategy.
     */
    @Override
    protected SessionAuthenticationStrategy sessionAuthenticationStrategy() {
        return new RegisterSessionAuthenticationStrategy(new SessionRegistryImpl());
    }

    @Bean
    public FilterRegistrationBean keycloakAuthenticationProcessingFilterRegistrationBean(KeycloakAuthenticationProcessingFilter filter) {
        var registrationBean = new FilterRegistrationBean(filter);
        registrationBean.setEnabled(false);
        return registrationBean;
    }

    @Bean
    public FilterRegistrationBean keycloakPreAuthActionsFilterRegistrationBean(KeycloakPreAuthActionsFilter filter) {
        var registrationBean = new FilterRegistrationBean(filter);
        registrationBean.setEnabled(false);
        return registrationBean;
    }

    @Override
    protected KeycloakLogoutHandler keycloakLogoutHandler() throws Exception {
        return super.keycloakLogoutHandler();
    }

    /*
     * Enable anonymous access for 'nonProtectedUrls' to bypass jwtDecoder validation for corresponding requests
     */
    @Override
    public void configure(WebSecurity web) {
        web.ignoring().mvcMatchers("/unprotected/**");
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        super.configure(http);

        KeycloakAuthenticationEntryPoint entryPoint = (KeycloakAuthenticationEntryPoint)authenticationEntryPoint();
        entryPoint.setLoginUri(keycloakLoginUrl);

        http.csrf().disable()
                .headers().frameOptions().disable()
                .and()
                .exceptionHandling()
                .authenticationEntryPoint(entryPoint)
                .and()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.ALWAYS)
                .and()
                .authorizeRequests()
                .mvcMatchers("/unprotected/**").permitAll()
                .mvcMatchers("/protected/**").authenticated()
                .and()
                .logout()
                .addLogoutHandler(keycloakLogoutHandler())
                .logoutRequestMatcher(new AntPathRequestMatcher(keycloakLogoutUrl))
                .logoutSuccessUrl("/login")
                .deleteCookies("JSESSIONID");
    }
}
