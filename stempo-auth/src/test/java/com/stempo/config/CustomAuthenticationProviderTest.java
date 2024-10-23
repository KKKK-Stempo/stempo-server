package com.stempo.config;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.stream.Collectors;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;

class CustomAuthenticationProviderTest {

    private CustomAuthenticationProvider authenticationProvider;
    private UserDetailsService userDetailsService;
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        userDetailsService = mock(UserDetailsService.class);
        passwordEncoder = mock(PasswordEncoder.class);
        authenticationProvider = new CustomAuthenticationProvider(userDetailsService, passwordEncoder);
    }

    @Test
    void 비밀번호가_없는_계정으로_인증에_성공한다() {
        // given
        UserDetails userDetails = User.withUsername("user")
                .password("")
                .authorities("ROLE_USER")
                .build();
        when(userDetailsService.loadUserByUsername("user")).thenReturn(userDetails);

        Authentication authentication = new UsernamePasswordAuthenticationToken("user", null);

        // when
        Authentication result = authenticationProvider.authenticate(authentication);

        // then
        assertThat(result.isAuthenticated()).isTrue();
        assertThat(result.getCredentials()).isNull();

        List<String> resultAuthorities = result.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        List<String> expectedAuthorities = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        assertThat(resultAuthorities).isEqualTo(expectedAuthorities);
    }

    @Test
    void 비밀번호가_있는_계정으로_인증에_성공한다() {
        // given
        String rawPassword = "password";
        String encodedPassword = "$2a$10$D9Q.iI1mUB7M8oC7a9tvUeC/Ux0XlxpWxjk3nFygWVa4U6S0VV/SK";
        UserDetails userDetails = User.withUsername("user")
                .password(encodedPassword) // 비밀번호가 있는 계정
                .authorities("ROLE_USER")
                .build();

        when(userDetailsService.loadUserByUsername("user")).thenReturn(userDetails);
        when(passwordEncoder.matches(rawPassword, encodedPassword)).thenReturn(true);

        Authentication authentication = new UsernamePasswordAuthenticationToken("user", rawPassword);

        // when
        Authentication result = authenticationProvider.authenticate(authentication);

        // then
        assertThat(result.isAuthenticated()).isTrue();
        assertThat(result.getCredentials()).isEqualTo(rawPassword);

        List<String> resultAuthorities = result.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        List<String> expectedAuthorities = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        assertThat(resultAuthorities).isEqualTo(expectedAuthorities);
    }

    @Test
    void 비밀번호가_틀린_경우_BadCredentialsException이_발생한다() {
        // given
        String rawPassword = "wrongpassword";
        String encodedPassword = "$2a$10$D9Q.iI1mUB7M8oC7a9tvUeC/Ux0XlxpWxjk3nFygWVa4U6S0VV/SK";
        UserDetails userDetails = User.withUsername("user")
                .password(encodedPassword)
                .authorities("ROLE_USER")
                .build();

        when(userDetailsService.loadUserByUsername("user")).thenReturn(userDetails);
        when(passwordEncoder.matches(rawPassword, encodedPassword)).thenReturn(false);

        Authentication authentication = new UsernamePasswordAuthenticationToken("user", rawPassword);

        // when & then
        assertThatThrownBy(() -> authenticationProvider.authenticate(authentication))
                .isInstanceOf(BadCredentialsException.class)
                .hasMessageContaining("자격 증명에 실패하였습니다.");
    }
}
