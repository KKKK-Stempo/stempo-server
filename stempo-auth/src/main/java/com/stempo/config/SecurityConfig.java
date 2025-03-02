package com.stempo.config;

import com.stempo.application.JwtTokenService;
import com.stempo.filter.CustomBasicAuthenticationFilter;
import com.stempo.filter.JwtAuthenticationFilter;
import com.stempo.logging.filter.MdcFilter;
import com.stempo.util.IpWhitelistValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.AuthorizeHttpRequestsConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
@Slf4j
public class SecurityConfig {

    private final AuthenticationManager authenticationManager;
    private final Customizer<AuthorizeHttpRequestsConfigurer<HttpSecurity>
        .AuthorizationManagerRequestMatcherRegistry> authorizeHttpRequestsCustomizer;
    private final JwtTokenService tokenService;
    private final IpWhitelistValidator ipWhitelistValidator;
    private final MdcFilter mdcFilter;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable)
            .sessionManagement(sessionManagement ->
                sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            .authorizeHttpRequests(
                authorizeHttpRequestsCustomizer
            )
            .addFilterBefore(
                mdcFilter,
                UsernamePasswordAuthenticationFilter.class
            )
            .addFilterBefore(
                new CustomBasicAuthenticationFilter(authenticationManager, ipWhitelistValidator),
                UsernamePasswordAuthenticationFilter.class
            )
            .addFilterBefore(
                new JwtAuthenticationFilter(tokenService),
                UsernamePasswordAuthenticationFilter.class
            )
            .exceptionHandling(httpSecurityExceptionHandlingConfigurer ->
                httpSecurityExceptionHandlingConfigurer
                    .accessDeniedHandler(new CustomAccessDeniedHandler())
                    .authenticationEntryPoint(new CustomAuthenticationEntryPoint())
            );
        return http.build();
    }
}
