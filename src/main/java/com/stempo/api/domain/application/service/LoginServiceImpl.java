package com.stempo.api.domain.application.service;

import com.stempo.api.domain.domain.model.User;
import com.stempo.api.domain.presentation.dto.request.LoginRequestDto;
import com.stempo.api.domain.presentation.dto.response.TokenInfo;
import com.stempo.api.global.auth.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
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

    private TokenInfo generateAndSaveToken(User loginUser) {
        TokenInfo tokenInfo = jwtTokenProvider.generateToken(loginUser.getId(), loginUser.getRole());
        redisTokenService.saveToken(loginUser.getId(), loginUser.getRole(), tokenInfo);
        return tokenInfo;
    }
}
