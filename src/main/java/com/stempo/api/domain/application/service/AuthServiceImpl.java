package com.stempo.api.domain.application.service;

import com.stempo.api.domain.application.exception.UserAlreadyExistsException;
import com.stempo.api.domain.domain.model.User;
import com.stempo.api.domain.presentation.dto.request.AuthRequestDto;
import com.stempo.api.domain.presentation.dto.response.TokenInfo;
import com.stempo.api.global.auth.exception.TokenForgeryException;
import com.stempo.api.global.auth.jwt.JwtTokenProvider;
import com.stempo.api.global.config.CustomAuthenticationProvider;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {

    private final UserService userService;
    private final JwtTokenProvider jwtTokenProvider;
    private final CustomAuthenticationProvider authenticationManager;
    private final PasswordEncoder passwordEncoder;

    @Override
    public TokenInfo registerUser(AuthRequestDto requestDto) {
        String deviceTag = requestDto.getDeviceTag();
        String password = requestDto.getPassword();

        if (userService.existsById(deviceTag)) {
            throw new UserAlreadyExistsException("User already exists.");
        }

        String finalPassword = StringUtils.isEmpty(password) ? null : passwordEncoder.encode(password);
        User user = User.create(deviceTag, finalPassword);
        userService.save(user);
        return jwtTokenProvider.generateToken(user.getDeviceTag(), user.getRole());
    }

    @Override
    public TokenInfo login(AuthRequestDto requestDto) {
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
