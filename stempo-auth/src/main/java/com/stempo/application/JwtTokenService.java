package com.stempo.application;

import com.stempo.dto.TokenInfo;
import com.stempo.model.Role;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.Authentication;

public interface JwtTokenService {

    TokenInfo generateToken(Authentication authentication);

    TokenInfo generateToken(String id, Role role);

    Authentication getAuthentication(String token);

    String resolveToken(HttpServletRequest request);

    boolean validateToken(String token);

    boolean validateTokenSilently(String token);

    boolean isRefreshToken(String token);
}
