package com.stempo.api.domain.application.service;

import com.stempo.api.domain.application.event.UserDeletedEvent;
import com.stempo.api.domain.application.exception.UserAlreadyExistsException;
import com.stempo.api.domain.domain.model.User;
import com.stempo.api.domain.presentation.dto.request.AuthRequestDto;
import com.stempo.api.domain.presentation.dto.response.TokenInfo;
import com.stempo.api.global.auth.exception.TokenForgeryException;
import com.stempo.api.global.auth.jwt.JwtTokenProvider;
import com.stempo.api.global.config.CustomAuthenticationProvider;
import com.stempo.api.global.util.EncryptionUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {

    private final UserService userService;
    private final JwtTokenProvider jwtTokenProvider;
    private final CustomAuthenticationProvider authenticationManager;
    private final PasswordEncoder passwordEncoder;
    private final EncryptionUtil encryptionUtil;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    @Transactional
    public TokenInfo registerUser(AuthRequestDto requestDto) {
        String deviceTag = encryptDeviceTag(requestDto.getDeviceTag());
        String password = encryptPassword(requestDto.getPassword());

        if (userService.existsById(deviceTag)) {
            throw new UserAlreadyExistsException("User already exists.");
        }

        User user = User.create(deviceTag, password);
        userService.save(user);
        return jwtTokenProvider.generateToken(user.getDeviceTag(), user.getRole());
    }

    @Override
    @Transactional
    public String unregisterUser() {
        User user = userService.getCurrentUser();
        userService.delete(user);
        eventPublisher.publishEvent(new UserDeletedEvent(this, user.getDeviceTag()));
        return user.getDeviceTag();
    }

    @Override
    @Transactional
    public TokenInfo login(AuthRequestDto requestDto) {
        String deviceTag = encryptDeviceTag(requestDto.getDeviceTag());
        String password = requestDto.getPassword();

        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(deviceTag, password);
        Authentication authentication = authenticationManager.authenticate(authenticationToken);
        return jwtTokenProvider.generateToken(authentication);
    }

    @Override
    @Transactional
    public TokenInfo reissueToken(HttpServletRequest request) {
        String refreshToken = jwtTokenProvider.resolveToken(request);
        validateRefreshToken(refreshToken);
        return reissueToken(refreshToken);
    }

    private String encryptDeviceTag(String deviceTag) {
        return encryptionUtil.encryptWithHashedIV(deviceTag, deviceTag);
    }

    private String encryptPassword(String password) {
        return StringUtils.isEmpty(password) ? null : passwordEncoder.encode(password);
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
