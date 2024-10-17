package com.stempo.config;

import com.stempo.application.CustomUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

import java.util.Arrays;

@Configuration
@RequiredArgsConstructor
public class AuthenticationConfig {

    private final CustomUserDetailsService customUserDetailsService;
    private final WhitelistProperties whitelistProperties;

    // 화이트리스트용 DaoAuthenticationProvider 설정
    @Bean
    public DaoAuthenticationProvider whitelistAuthenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(whitelistUserDetailsService());
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    // 화이트리스트 사용자에 대한 InMemoryUserDetailsManager 설정
    @Bean
    public InMemoryUserDetailsManager whitelistUserDetailsService() {
        UserDetails user = User.withUsername(whitelistProperties.getAccount().getUsername())
                .password(passwordEncoder().encode(whitelistProperties.getAccount().getPassword()))
                .roles(whitelistProperties.getAccount().getRole())
                .build();
        return new InMemoryUserDetailsManager(user);
    }

    // 일반 사용자용 CustomAuthenticationProvider 설정
    @Bean
    public CustomAuthenticationProvider customAuthenticationProvider() {
        return new CustomAuthenticationProvider(customUserDetailsService, passwordEncoder());
    }

    // BCryptPasswordEncoder 설정
    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // 두 가지 인증 프로바이더를 함께 사용하는 AuthenticationManager 설정
    @Bean
    public AuthenticationManager authenticationManager() {
        return new ProviderManager(Arrays.asList(
                customAuthenticationProvider(), // 일반 사용자용 프로바이더
                whitelistAuthenticationProvider() // 화이트리스트용 프로바이더
        ));
    }
}
