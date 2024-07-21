package com.stempo.api.global.auth.filter;

import com.stempo.api.domain.application.service.RedisTokenService;
import com.stempo.api.domain.domain.model.RedisToken;
import com.stempo.api.global.auth.jwt.JwtTokenProvider;
import com.stempo.api.global.util.ResponseUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.GenericFilterBean;

import java.io.IOException;

@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends GenericFilterBean {

    private final RedisTokenService redisTokenService;
    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        HttpServletResponse httpServletResponse = (HttpServletResponse) response;
        if (!authenticateToken(httpServletRequest, httpServletResponse)) {
            return;
        }
        chain.doFilter(request, response);
    }

    private boolean authenticateToken(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String token = jwtTokenProvider.resolveToken(request);
        if (token != null && jwtTokenProvider.validateToken(token)) {
            RedisToken redisToken = jwtTokenProvider.isRefreshToken(token) ? redisTokenService.findByRefreshToken(token) : redisTokenService.findByAccessToken(token);
            if (redisToken == null) {
                log.warn("Token not found in redis");
                ResponseUtil.sendErrorResponse(response, HttpServletResponse.SC_UNAUTHORIZED);
                return false;
            }
            Authentication authentication = jwtTokenProvider.getAuthentication(token);
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }
        return true;
    }
}
