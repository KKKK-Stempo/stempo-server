package com.stempo.api.domain.application.service;

import com.stempo.api.domain.domain.model.RedisToken;
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
    private final RedisTokenService redisTokenService;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public TokenInfo login(LoginRequestDto requestDto) {
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(requestDto.getId(), requestDto.getPassword());
        authenticationManager.authenticate(authenticationToken);
        User loginUser = userService.findByIdOrThrow(requestDto.getId());
        return generateAndSaveToken(loginUser);
    }

    @Override
    public TokenInfo reissueToken(HttpServletRequest request) {
        String refreshToken = jwtTokenProvider.resolveToken(request);
        Authentication authentication = jwtTokenProvider.getAuthentication(refreshToken);
        RedisToken redisToken = redisTokenService.findByRefreshToken(refreshToken);

        validateUserExistence(authentication);

        TokenInfo newTokenInfo = jwtTokenProvider.generateToken(redisToken.getId(), redisToken.getRole());
        redisTokenService.saveToken(redisToken.getId(), redisToken.getRole(), newTokenInfo);
        return newTokenInfo;
    }

    private TokenInfo generateAndSaveToken(User loginUser) {
        TokenInfo tokenInfo = jwtTokenProvider.generateToken(loginUser.getId(), loginUser.getRole());
        redisTokenService.saveToken(loginUser.getId(), loginUser.getRole(), tokenInfo);
        return tokenInfo;
    }

    private void validateUserExistence(Authentication authentication) {
        String id = authentication.getName();
        if (!userService.existsById(id)) {
            throw new TokenForgeryException("Non-existent user token.");
        }
    }
}
