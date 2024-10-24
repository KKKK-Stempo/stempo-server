package com.stempo.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.stempo.application.JwtTokenService;
import com.stempo.dto.TokenInfo;
import com.stempo.exception.TokenForgeryException;
import com.stempo.model.Role;
import com.stempo.model.User;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.Authentication;

class TokenServiceTest {

    @Mock
    private JwtTokenService tokenService;

    @Mock
    private UserService userService;

    @Mock
    private HttpServletRequest request;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private TokenService tokenServiceImpl;

    private TokenInfo tokenInfo;
    private User user;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        user = User.builder().deviceTag("user-device").role(Role.USER).build();
        tokenInfo = TokenInfo.create("access-token", "refresh-token");
    }

    @Test
    void 리프레시_토큰이_유효하면_토큰을_재발급한다() {
        // given
        String refreshToken = "valid-refresh-token";
        when(tokenService.resolveToken(request)).thenReturn(refreshToken);
        when(tokenService.isRefreshToken(refreshToken)).thenReturn(true);
        when(tokenService.getAuthentication(refreshToken)).thenReturn(authentication);
        when(authentication.getName()).thenReturn("user-device");
        when(userService.findById("user-device")).thenReturn(Optional.of(user));
        when(tokenService.generateToken("user-device", Role.USER)).thenReturn(tokenInfo);

        // when
        TokenInfo result = tokenServiceImpl.reissueToken(request);

        // then
        assertThat(result).isEqualTo(tokenInfo);
        verify(tokenService).resolveToken(request);
        verify(tokenService).isRefreshToken(refreshToken);
        verify(tokenService).getAuthentication(refreshToken);
        verify(userService).findById("user-device");
        verify(tokenService).generateToken("user-device", Role.USER);
    }

    @Test
    void 리프레시_토큰이_유효하지_않으면_예외를_발생시킨다() {
        // given
        String invalidRefreshToken = "invalid-refresh-token";
        when(tokenService.resolveToken(request)).thenReturn(invalidRefreshToken);
        when(tokenService.isRefreshToken(invalidRefreshToken)).thenReturn(false);

        // when, then
        assertThatThrownBy(() -> tokenServiceImpl.reissueToken(request))
                .isInstanceOf(TokenForgeryException.class)
                .hasMessage("Invalid refresh token.");

        verify(tokenService).resolveToken(request);
        verify(tokenService).isRefreshToken(invalidRefreshToken);
        verify(tokenService, never()).getAuthentication(anyString());
    }

    @Test
    void 토큰에서_사용자를_찾을_수_없으면_예외를_발생시킨다() {
        // given
        String refreshToken = "valid-refresh-token";
        when(tokenService.resolveToken(request)).thenReturn(refreshToken);
        when(tokenService.isRefreshToken(refreshToken)).thenReturn(true);
        when(tokenService.getAuthentication(refreshToken)).thenReturn(authentication);
        when(authentication.getName()).thenReturn("unknown-device");
        when(userService.findById("unknown-device")).thenReturn(Optional.empty());

        // when, then
        assertThatThrownBy(() -> tokenServiceImpl.reissueToken(request))
                .isInstanceOf(TokenForgeryException.class)
                .hasMessage("Non-existent user token.");

        verify(tokenService).resolveToken(request);
        verify(tokenService).isRefreshToken(refreshToken);
        verify(tokenService).getAuthentication(refreshToken);
        verify(userService).findById("unknown-device");
        verify(tokenService, never()).generateToken(anyString(), any());
    }
}
