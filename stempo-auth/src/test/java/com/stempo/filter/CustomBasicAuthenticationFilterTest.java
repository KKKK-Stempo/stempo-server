package com.stempo.filter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.stempo.config.WhitelistProperties;
import com.stempo.util.IpWhitelistValidator;
import com.stempo.util.WhitelistPathMatcher;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.util.Base64;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

@ExtendWith(MockitoExtension.class)
class CustomBasicAuthenticationFilterTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private IpWhitelistValidator ipWhitelistValidator;

    @Mock
    private WhitelistProperties whitelistProperties;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    private CustomBasicAuthenticationFilter filter;

    @BeforeEach
    void setUp() {
        WhitelistProperties.Patterns patterns = new WhitelistProperties.Patterns();
        patterns.setActuator(new String[]{"/actuator/*"});
        patterns.setApiDocs(new String[]{"/docs/*", "/swagger/*"});

        when(whitelistProperties.getPatterns()).thenReturn(patterns);

        WhitelistPathMatcher whitelistPathMatcher = new WhitelistPathMatcher(whitelistProperties);
        whitelistPathMatcher.afterPropertiesSet();

        filter = new CustomBasicAuthenticationFilter(authenticationManager, ipWhitelistValidator);
    }

    @Test
    void 화이트리스트_경로가_아니면_다음_필터로_넘긴다() throws Exception {
        // given
        when(request.getRequestURI()).thenReturn("/non-whitelist-path");

        // when
        filter.doFilterInternal(request, response, filterChain);

        // then
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void 화이트리스트_IP가_아니면_401_응답을_보낸다() throws Exception {
        // given
        when(request.getRequestURI()).thenReturn("/docs");
        when(ipWhitelistValidator.isIpWhitelisted(anyString())).thenReturn(false);
        when(response.getWriter()).thenReturn(new PrintWriter(System.out));

        // when
        filter.doFilterInternal(request, response, filterChain);

        // then
        verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    }

    @Test
    void 올바른_IP와_인증정보시_다음_필터로_넘긴다() throws Exception {
        // given
        when(request.getRequestURI()).thenReturn("/docs");
        when(ipWhitelistValidator.isIpWhitelisted(anyString())).thenReturn(true);
        when(request.getHeader("Authorization")).thenReturn(
                "Basic " + Base64.getEncoder().encodeToString("user:password".getBytes()));

        Authentication auth = new UsernamePasswordAuthenticationToken("user", "password");
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(auth);

        // when
        filter.doFilterInternal(request, response, filterChain);

        // then
        verify(filterChain).doFilter(request, response);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        assertThat(authentication).isNotNull();
        assertThat(authentication.getName()).isEqualTo("user");
    }

    @Test
    void 잘못된_인증정보시_401_응답을_보낸다() throws Exception {
        // given
        when(request.getRequestURI()).thenReturn("/docs");
        when(ipWhitelistValidator.isIpWhitelisted(anyString())).thenReturn(true);
        when(request.getHeader("Authorization")).thenReturn(
                "Basic " + Base64.getEncoder().encodeToString("user:wrongpassword".getBytes()));
        when(response.getWriter()).thenReturn(new PrintWriter(System.out));

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Invalid credentials"));

        // when
        filter.doFilterInternal(request, response, filterChain);

        // then
        verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    }

    @Test
    void 인증헤더가_없으면_401_응답을_보낸다() throws Exception {
        // given
        when(request.getRequestURI()).thenReturn("/docs");
        when(ipWhitelistValidator.isIpWhitelisted(anyString())).thenReturn(true);
        when(request.getHeader("Authorization")).thenReturn(null);
        when(response.getWriter()).thenReturn(new PrintWriter(System.out));

        // when
        filter.doFilterInternal(request, response, filterChain);

        // then
        verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    }
}
