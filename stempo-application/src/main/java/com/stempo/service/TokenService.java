package com.stempo.service;

import com.stempo.application.JwtTokenService;
import com.stempo.dto.TokenInfo;
import com.stempo.exception.TokenForgeryException;
import com.stempo.model.User;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TokenService {

    private final JwtTokenService tokenService;
    private final UserService userService;

    @Transactional
    public TokenInfo reissueToken(HttpServletRequest request) {
        String refreshToken = tokenService.resolveToken(request);
        validateRefreshToken(refreshToken);
        return reissueToken(refreshToken);
    }

    private void validateRefreshToken(String refreshToken) {
        if (!tokenService.isRefreshToken(refreshToken)) {
            throw new TokenForgeryException("Invalid refresh token.");
        }
    }

    private TokenInfo reissueToken(String refreshToken) {
        Authentication authentication = tokenService.getAuthentication(refreshToken);
        User user = getTokenUserInfo(authentication);
        return tokenService.generateToken(user.getDeviceTag(), user.getRole());
    }

    private User getTokenUserInfo(Authentication authentication) {
        String id = authentication.getName();
        return userService.findById(id)
                .orElseThrow(() -> new TokenForgeryException("Non-existent user token."));
    }
}
