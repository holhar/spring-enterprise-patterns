package de.holhar.spring.patterns.oidc;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.oauth2.client.oidc.web.logout.OidcClientInitiatedLogoutSuccessHandler;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Value("#{'${app.unProtectedUrls:[]}'.split(',')}")
    private String[] nonProtectedUrls;

    @Value("#{'${app.csrfDisabledUrls}'.split(',')}")
    protected List<String> csrfDisabledUrls;

    @Autowired
    private ClientRegistrationRepository clientRegistrationRepository;

    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers(nonProtectedUrls);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.headers().frameOptions().disable()
                .and()
                .authorizeRequests(authorizeRequests -> authorizeRequests
                        .antMatchers(nonProtectedUrls).permitAll()
                        .mvcMatchers("/protected/**").authenticated()
                )
                .oauth2Login(oauthLogin -> oauthLogin
                        .authorizationEndpoint()
                        .baseUri("/login/oauth2/authorization")
                        .and()
                        .redirectionEndpoint()
                        .baseUri("/login/oauth2/code")
                )
                .logout(logout -> logout
                        .deleteCookies("JSESSIONID")
                        .clearAuthentication(true)
                        .invalidateHttpSession(true)
                        .logoutSuccessHandler(new OidcClientInitiatedLogoutSuccessHandler(clientRegistrationRepository))
                        .permitAll()
                )
                .csrf()
                .requireCsrfProtectionMatcher(csrfProtectedUrlsMatcher());
    }

    private RequestMatcher csrfProtectedUrlsMatcher() {
        final Pattern protectedMethods = Pattern.compile("^(PATCH|POST|PUT|DELETE)$");
        // Stream.of(...) might be a list of resources externally configured
        final List<AntPathRequestMatcher> protectedUrls = Stream.of("this", "that", "...")
                .filter(url -> !csrfDisabledUrls.contains(url))
                .map(url -> new AntPathRequestMatcher("/basePath/" + url + "/**"))
                .collect(Collectors.toList());

        return (request -> {
            for (AntPathRequestMatcher protectedUrl : protectedUrls) {
                if (protectedUrl.matches(request) && protectedMethods.matcher(request.getMethod()).matches()) {
                    return true;
                }
            }
            return false;
        });
    }
}
