package com.stempo.api.domain.application.service;

import com.stempo.api.domain.domain.model.User;
import com.stempo.api.domain.presentation.dto.request.LoginRequestDto;
import com.stempo.api.domain.presentation.dto.response.TokenInfo;
import com.stempo.api.global.auth.exception.TokenForgeryException;
import com.stempo.api.global.auth.jwt.JwtTokenProvider;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LoginServiceImpl implements LoginService {

    private final UserService userService;
    private final JwtTokenProvider jwtTokenProvider;
    private final AuthenticationManager authenticationManager;

    @Override
    public TokenInfo login(LoginRequestDto requestDto) {
        String deviceTag = requestDto.getDeviceTag();
        String password = requestDto.getPassword();

        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(deviceTag, password);
        Authentication authentication = authenticationManager.authenticate(authenticationToken);
        return jwtTokenProvider.generateToken(authentication);
    }

    @Override
    public TokenInfo reissueToken(HttpServletRequest request) {
        String refreshToken = jwtTokenProvider.resolveToken(request);
        validateRefreshToken(refreshToken);
        return reissueToken(refreshToken);
    }

    private void validateRefreshToken(String refreshToken) {
        if (!jwtTokenProvider.isRefreshToken(refreshToken)) {
            throw new TokenForgeryException("Invalid refresh token.");
        }
    }

    private TokenInfo reissueToken(String refreshToken) {
        Authentication authentication = jwtTokenProvider.getAuthentication(refreshToken);
        User user = getTokenUserInfo(authentication);
        return jwtTokenProvider.generateToken(user.getDeviceTag(), user.getRole());
    }

    private User getTokenUserInfo(Authentication authentication) {
        String id = authentication.getName();
        return userService.findById(id)
                .orElseThrow(() -> new TokenForgeryException("Non-existent user token."));
    }
}
