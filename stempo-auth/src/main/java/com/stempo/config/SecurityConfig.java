package com.stempo.config;

import com.stempo.application.JwtTokenService;
import com.stempo.filter.CustomBasicAuthenticationFilter;
import com.stempo.filter.JwtAuthenticationFilter;
import com.stempo.filter.MDCFilter;
import com.stempo.util.IpWhitelistValidator;
import com.stempo.util.ResponseUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.AuthorizeHttpRequestsConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.AuthenticationException;
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
    private final MDCFilter mdcFilter;

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
                    .authenticationEntryPoint(this::handleException)
                    .accessDeniedHandler(this::handleException)
            );
        return http.build();
    }

    private void handleException(HttpServletRequest request, HttpServletResponse response, Exception exception)
        throws IOException {
        String message;
        int statusCode;

        if (exception instanceof AuthenticationException) {
            message = "인증되지 않은 사용자의 비정상적인 접근이 감지되었습니다.";
            statusCode = HttpServletResponse.SC_UNAUTHORIZED;
        } else if (exception instanceof AccessDeniedException) {
            message = "권한이 없는 엔드포인트에 대한 접근이 감지되었습니다.";
            statusCode = HttpServletResponse.SC_FORBIDDEN;
        } else {
            message = "비정상적인 접근이 감지되었습니다.";
            statusCode = HttpServletResponse.SC_BAD_REQUEST;
        }

        MDC.put("exceptionClass", exception.getClass().getName());
        MDC.put("exceptionMessage", message);

        ResponseUtils.sendErrorResponse(response, statusCode);
    }
}
