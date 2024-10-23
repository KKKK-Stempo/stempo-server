package com.stempo.filter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.stempo.application.JwtTokenService;
import com.stempo.util.WhitelistPathMatcher;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

class JwtAuthenticationFilterTest {

    @Mock
    private JwtTokenService tokenService;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        jwtAuthenticationFilter = new JwtAuthenticationFilter(tokenService);
        SecurityContextHolder.clearContext();
    }

    @Test
    void 화이트리스트_경로면_다음_필터로_넘긴다() throws Exception {
        // given
        when(request.getRequestURI()).thenReturn("/whitelist-path");

        try (MockedStatic<WhitelistPathMatcher> mockedMatcher = mockStatic(WhitelistPathMatcher.class)) {
            mockedMatcher.when(() -> WhitelistPathMatcher.isWhitelistRequest("/whitelist-path")).thenReturn(true);

            // when
            jwtAuthenticationFilter.doFilter(request, response, filterChain);

            // then
            verify(filterChain).doFilter(request, response);
            verify(tokenService, never()).resolveToken(request);
        }
    }

    @Test
    void 토큰이_유효하면_인증정보를_설정하고_다음_필터로_넘긴다() throws Exception {
        // given
        String token = "validToken";
        Authentication authentication = mock(Authentication.class);

        when(request.getRequestURI()).thenReturn("/api/path");
        when(tokenService.resolveToken(request)).thenReturn(token);
        when(tokenService.validateTokenSilently(token)).thenReturn(true);
        when(tokenService.getAuthentication(token)).thenReturn(authentication);

        // when
        boolean isWhitelist = WhitelistPathMatcher.isWhitelistRequest("/api/path");

        if (!isWhitelist) {
            jwtAuthenticationFilter.doFilter(request, response, filterChain);
        }

        // then
        verify(tokenService).resolveToken(request);
        verify(tokenService).validateTokenSilently(token);
        verify(tokenService).getAuthentication(token);
        verify(filterChain).doFilter(request, response);
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isEqualTo(authentication);
    }

    @Test
    void 토큰이_없는_경우_그대로_다음_필터로_넘긴다() throws Exception {
        // given
        when(request.getRequestURI()).thenReturn("/api/path");
        when(tokenService.resolveToken(request)).thenReturn(null);

        // when
        boolean isWhitelist = WhitelistPathMatcher.isWhitelistRequest("/api/path");

        if (!isWhitelist) {
            jwtAuthenticationFilter.doFilter(request, response, filterChain);
        }

        // then
        verify(tokenService).resolveToken(request);
        verify(filterChain).doFilter(request, response);
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    }

    @Test
    void 토큰이_유효하지_않으면_그대로_다음_필터로_넘긴다() throws Exception {
        // given
        String invalidToken = "invalidToken";

        when(request.getRequestURI()).thenReturn("/api/path");
        when(tokenService.resolveToken(request)).thenReturn(invalidToken);
        when(tokenService.validateTokenSilently(invalidToken)).thenReturn(false);

        // when
        boolean isWhitelist = WhitelistPathMatcher.isWhitelistRequest("/api/path");

        if (!isWhitelist) {
            jwtAuthenticationFilter.doFilter(request, response, filterChain);
        }

        // then
        verify(tokenService).resolveToken(request);
        verify(tokenService).validateTokenSilently(invalidToken);
        verify(filterChain).doFilter(request, response);
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    }
}
