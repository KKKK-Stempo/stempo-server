package com.stempo.config;

import com.stempo.application.JwtTokenService;
import com.stempo.filter.JwtAuthenticationFilter;
import com.stempo.util.ApiLogger;
import com.stempo.util.HttpReqResUtils;
import com.stempo.util.ResponseUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtTokenService tokenService;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(sessionManagement ->
                        sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .authorizeHttpRequests(authorizeRequests ->
                        authorizeRequests.anyRequest().permitAll()
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

    private void handleException(HttpServletRequest request, HttpServletResponse response, Exception exception) throws IOException {
        String clientIpAddress = HttpReqResUtils.getClientIpAddressIfServletRequestExist();
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

        ApiLogger.logRequest(request, response, clientIpAddress, message);
        ResponseUtils.sendErrorResponse(response, statusCode);
    }
}
