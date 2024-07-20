package com.stempo.api.global.config;

import com.stempo.api.global.auth.application.CustomUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;

@Configuration
@RequiredArgsConstructor
public class AuthenticationConfig {

    private final CustomUserDetailsService customUserDetailsService;
    private final PasswordEncoder passwordEncoder;

    @Bean
    public AuthenticationManager authenticationManager() {
        DaoAuthenticationProvider loginProvider = new DaoAuthenticationProvider();
        loginProvider.setUserDetailsService(customUserDetailsService);
        loginProvider.setPasswordEncoder(passwordEncoder);
        return new ProviderManager(List.of(loginProvider));
    }
}
