package com.stempo.test.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class TestSecurityConfig {

    public static final String[] PERMIT_ALL_API_ENDPOINTS_POST = {
        "/api/v1/auth/register",
        "/api/v1/auth/login",
        "/api/v1/auth/two-factor-authentication"
    };

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable)
            .sessionManagement(sessionManagement ->
                sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            .authorizeHttpRequests(authorizeRequests ->
                authorizeRequests
                    .requestMatchers(HttpMethod.POST, PERMIT_ALL_API_ENDPOINTS_POST).permitAll()
                    .anyRequest().authenticated()
            )
            .exceptionHandling(exceptionHandling ->
                exceptionHandling.authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED))
            );
        return http.build();
    }
}
