package com.stempo.config;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import com.stempo.application.CustomUserDetailsService;
import com.stempo.test.config.TestConfiguration;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(classes = {AuthenticationConfig.class, TestConfiguration.class})
@ActiveProfiles("test")
class AuthenticationConfigTest {

    @Autowired
    private AuthenticationManager authenticationManager;

    @MockBean
    private CustomUserDetailsService customUserDetailsService;

    @Autowired
    private WhitelistProperties whitelistProperties;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        String username = whitelistProperties.getAccount().getUsername();
        String password = whitelistProperties.getAccount().getPassword();
        String role = whitelistProperties.getAccount().getRole();

        UserDetails whitelistUser = org.springframework.security.core.userdetails.User.withUsername(username)
                .password(passwordEncoder.encode(password))
                .roles(role)
                .build();

        when(customUserDetailsService.loadUserByUsername("testuser")).thenReturn(whitelistUser);
    }

    @Test
    void 화이트리스트_사용자가_성공적으로_인증된다() {
        // given
        Authentication authRequest = new UsernamePasswordAuthenticationToken("testuser", "testpass");

        // when
        Authentication authResult = authenticationManager.authenticate(authRequest);

        // then
        assertThat(authResult.isAuthenticated()).isTrue();
        assertThat(authResult.getName()).isEqualTo("testuser");
    }

    @Test
    void 비밀번호가_틀린_경우_인증에_실패한다() {
        // given
        Authentication authRequest = new UsernamePasswordAuthenticationToken("testuser", "wrongPass");

        // when, then
        assertThatThrownBy(() -> authenticationManager.authenticate(authRequest))
                .isInstanceOf(BadCredentialsException.class);
    }

    @Test
    void 비밀번호가_없는_경우_인증에_실패한다() {
        // given
        Authentication authRequest = new UsernamePasswordAuthenticationToken("testuser", null);

        // when, then
        assertThatThrownBy(() -> authenticationManager.authenticate(authRequest))
                .isInstanceOf(BadCredentialsException.class);
    }

    @Test
    void 인증되지_않은_사용자가_로그인_시도하면_실패한다() {
        // given
        when(customUserDetailsService.loadUserByUsername("unknownUser")).thenReturn(null);
        Authentication authRequest = new UsernamePasswordAuthenticationToken("unknownUser", "testpass");

        // when, then
        assertThatThrownBy(() -> authenticationManager.authenticate(authRequest))
                .isInstanceOf(BadCredentialsException.class);
    }

    @Test
    void 사용자_권한이_없는_경우_로그인_성공하지만_권한이_없다() {
        // given
        UserDetails userWithoutRole = org.springframework.security.core.userdetails.User.withUsername("noroleuser")
                .password(passwordEncoder.encode("norolepass"))
                .authorities(List.of())
                .build();

        when(customUserDetailsService.loadUserByUsername("noroleuser")).thenReturn(userWithoutRole);
        Authentication authRequest = new UsernamePasswordAuthenticationToken("noroleuser", "norolepass");

        // when
        Authentication authResult = authenticationManager.authenticate(authRequest);

        // then
        assertThat(authResult.isAuthenticated()).isTrue();
        assertThat(authResult.getAuthorities()).isEmpty();
    }
}
