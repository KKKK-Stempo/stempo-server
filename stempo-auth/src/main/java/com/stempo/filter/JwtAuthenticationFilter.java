package com.stempo.filter;

import com.stempo.application.JwtTokenService;
import com.stempo.util.WhitelistPathMatcher;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.GenericFilterBean;

import java.io.IOException;
import java.util.Objects;

@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends GenericFilterBean {

    private final JwtTokenService tokenService;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        String path = httpServletRequest.getRequestURI();

        // 화이트리스트 경로인 경우 JWT 인증 대신 Basic Auth 인증을 사용
        if (WhitelistPathMatcher.isWhitelistRequest(path)) {
            chain.doFilter(request, response);
            return;
        }

        if (!authenticateToken(httpServletRequest)) {
            return;
        }

        chain.doFilter(request, response);
    }

    private boolean authenticateToken(HttpServletRequest request) {
        String token = tokenService.resolveToken(request);
        if (Objects.nonNull(token) && tokenService.validateTokenSilently(token)) {
            Authentication authentication = tokenService.getAuthentication(token);
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }
        return true;
    }
}
