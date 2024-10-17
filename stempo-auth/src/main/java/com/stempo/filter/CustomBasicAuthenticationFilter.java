package com.stempo.filter;

import com.stempo.util.ResponseUtils;
import com.stempo.util.WhitelistPathMatcher;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import java.io.IOException;
import java.util.Base64;

@Slf4j
public class CustomBasicAuthenticationFilter extends BasicAuthenticationFilter {

    private final AuthenticationManager whitelistAuthenticationManager;

    public CustomBasicAuthenticationFilter(AuthenticationManager whitelistAuthenticationManager) {
        super(whitelistAuthenticationManager);
        this.whitelistAuthenticationManager = whitelistAuthenticationManager;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        String path = request.getRequestURI();
        if (!WhitelistPathMatcher.isWhitelistRequest(path)) {
            chain.doFilter(request, response);
            return;
        }

        if (!authenticateUserCredentials(request, response)) {
            return;
        }

        super.doFilterInternal(request, response, chain);
    }

    private boolean authenticateUserCredentials(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String authorizationHeader = request.getHeader("Authorization");
        if (authorizationHeader == null || !authorizationHeader.startsWith("Basic ")) {
            response.setHeader("WWW-Authenticate", "Basic realm=\"Please enter your username and password\"");
            ResponseUtils.sendErrorResponse(response, HttpServletResponse.SC_UNAUTHORIZED);
            return false;
        }

        String[] credentials = decodeCredentials(authorizationHeader);
        if (credentials.length < 2) {
            ResponseUtils.sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST);
            return false;
        }

        String username = credentials[0];
        String password = credentials[1];
        UsernamePasswordAuthenticationToken authRequest = new UsernamePasswordAuthenticationToken(username, password);

        try {
            // 화이트리스트용 AuthenticationManager로 인증 처리
            Authentication authentication = whitelistAuthenticationManager.authenticate(authRequest);
            SecurityContextHolder.getContext().setAuthentication(authentication);
        } catch (BadCredentialsException e) {
            ResponseUtils.sendErrorResponse(response, HttpServletResponse.SC_UNAUTHORIZED);
            return false;
        }

        return true;
    }

    @NotNull
    private static String[] decodeCredentials(String authorizationHeader) {
        String base64Credentials = authorizationHeader.substring("Basic ".length());
        String credentials = new String(Base64.getDecoder().decode(base64Credentials));
        return credentials.split(":", 2);
    }
}
