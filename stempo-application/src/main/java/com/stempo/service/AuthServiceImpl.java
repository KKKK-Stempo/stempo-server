package com.stempo.service;

import com.stempo.application.JwtTokenService;
import com.stempo.config.AesConfig;
import com.stempo.config.CustomAuthenticationProvider;
import com.stempo.dto.TokenInfo;
import com.stempo.dto.request.AuthRequestDto;
import com.stempo.event.UserDeletedEvent;
import com.stempo.exception.TokenForgeryException;
import com.stempo.exception.UserAlreadyExistsException;
import com.stempo.model.User;
import com.stempo.util.EncryptionUtils;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {

    private final UserService userService;
    private final JwtTokenService tokenService;
    private final CustomAuthenticationProvider authenticationManager;
    private final PasswordEncoder passwordEncoder;
    private final EncryptionUtils encryptionUtils;
    private final ApplicationEventPublisher eventPublisher;
    private final AesConfig aesConfig;

    @Override
    @Transactional
    public TokenInfo registerUser(AuthRequestDto requestDto) {
        String deviceTag = encryptDeviceTag(requestDto.getDeviceTag());
        String password = encryptPassword(requestDto.getPassword().trim());

        if (userService.existsById(deviceTag)) {
            throw new UserAlreadyExistsException("User already exists.");
        }

        User user = User.create(deviceTag, password);
        userService.save(user);
        return tokenService.generateToken(user.getDeviceTag(), user.getRole());
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
        return tokenService.generateToken(authentication);
    }

    @Override
    @Transactional
    public TokenInfo reissueToken(HttpServletRequest request) {
        String refreshToken = tokenService.resolveToken(request);
        validateRefreshToken(refreshToken);
        return reissueToken(refreshToken);
    }

    private String encryptDeviceTag(String deviceTag) {
        return encryptionUtils.encryptWithHashedIV(deviceTag, aesConfig.getDeviceTagSecretKey());
    }

    private String encryptPassword(String password) {
        return !StringUtils.hasLength(password) ? null : passwordEncoder.encode(password);
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
