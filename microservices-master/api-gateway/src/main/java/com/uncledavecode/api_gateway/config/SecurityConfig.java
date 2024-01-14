package com.uncledavecode.api_gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.client.oidc.web.server.logout.OidcClientInitiatedServerLogoutSuccessHandler;
import org.springframework.security.oauth2.client.registration.ReactiveClientRegistrationRepository;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authentication.logout.ServerLogoutSuccessHandler;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http, ServerLogoutSuccessHandler handler){
        http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .authorizeExchange(auth -> {
                    auth.pathMatchers("/actuator/**").permitAll();
                    auth.anyExchange().permitAll();
                })
                .logout(
                      logout -> logout.logoutSuccessHandler(handler)
                )
                .oauth2Login(Customizer.withDefaults());
        return http.build();
    }

    @Bean
    public ServerLogoutSuccessHandler keycloakLogoutSuccessHandler(ReactiveClientRegistrationRepository repository) {

        OidcClientInitiatedServerLogoutSuccessHandler oidcLogoutSuccessHandler =
                new OidcClientInitiatedServerLogoutSuccessHandler(repository);

        oidcLogoutSuccessHandler.setPostLogoutRedirectUri("{baseUrl}");

        return oidcLogoutSuccessHandler;
    }
}
