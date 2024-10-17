package com.stempo.service;

import com.stempo.application.JwtTokenService;
import com.stempo.config.AesConfig;
import com.stempo.config.CustomAuthenticationProvider;
import com.stempo.dto.TokenInfo;
import com.stempo.dto.request.AuthRequestDto;
import com.stempo.dto.request.TwoFactorAuthenticationRequestDto;
import com.stempo.event.UserDeletedEvent;
import com.stempo.exception.InvalidPasswordException;
import com.stempo.exception.TokenForgeryException;
import com.stempo.exception.UserAlreadyExistsException;
import com.stempo.model.Role;
import com.stempo.model.User;
import com.stempo.util.EncryptionUtils;
import com.stempo.util.PasswordValidator;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.authentication.BadCredentialsException;
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
    private final TotpAuthenticatorService authenticatorService;
    private final JwtTokenService tokenService;
    private final CustomAuthenticationProvider authenticationManager;
    private final PasswordEncoder passwordEncoder;
    private final PasswordValidator passwordValidator;
    private final EncryptionUtils encryptionUtils;
    private final ApplicationEventPublisher eventPublisher;
    private final AesConfig aesConfig;

    @Override
    @Transactional
    public TokenInfo registerUser(AuthRequestDto requestDto) {
        String deviceTag = encryptDeviceTag(requestDto.getDeviceTag());
        String password = requestDto.getPassword();

        validatePassword(password, deviceTag);
        if (userService.existsById(deviceTag)) {
            throw new UserAlreadyExistsException("User already exists.");
        }

        User user = createUser(deviceTag, password);
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
    @Transactional(noRollbackFor = {BadCredentialsException.class, InvalidPasswordException.class})
    public Object login(AuthRequestDto requestDto) {
        String deviceTag = encryptDeviceTag(requestDto.getDeviceTag());
        String password = requestDto.getPassword();

        userService.handleAccountLock(deviceTag);
        validatePassword(password, deviceTag);

        return authenticateUserAndGenerateToken(deviceTag, password);
    }

    @Override
    @Transactional
    public TokenInfo reissueToken(HttpServletRequest request) {
        String refreshToken = tokenService.resolveToken(request);
        validateRefreshToken(refreshToken);
        return reissueToken(refreshToken);
    }

    @Override
    public TokenInfo authenticate(TwoFactorAuthenticationRequestDto requestDto) {
        String deviceTag = encryptDeviceTag(requestDto.getDeviceTag());
        String totp = requestDto.getTotp();

        if (!authenticatorService.isAuthenticatorValid(deviceTag, totp)) {
            throw new BadCredentialsException("Invalid TOTP code.");
        }

        Role role = userService.getById(deviceTag).getRole();
        return tokenService.generateToken(deviceTag, role);
    }

    @Override
    public String resetAuthenticator(String deviceTag) {
        String encryptedDeviceTag = encryptDeviceTag(deviceTag);
        return authenticatorService.resetAuthenticator(encryptedDeviceTag);
    }

    private Object authenticateUserAndGenerateToken(String deviceTag, String password) {
        try {
            UsernamePasswordAuthenticationToken authenticationToken =
                    new UsernamePasswordAuthenticationToken(deviceTag, password);
            Authentication authentication = authenticationManager.authenticate(authenticationToken);

            // 로그인 성공 시 실패 횟수 초기화
            userService.resetFailedAttempts(deviceTag);
            boolean isAdmin = userService.getById(deviceTag).isAdmin();
            if (isAdmin) {
                if (!authenticatorService.isAuthenticatorExist(deviceTag)) {
                    return authenticatorService.generateSecretKey(deviceTag);
                } else {
                    return null;
                }
            }
            return tokenService.generateToken(authentication);
        } catch (Exception e) {
            userService.handleFailedLogin(deviceTag);
            throw new BadCredentialsException("Invalid deviceTag or password.");
        }
    }

    private void validatePassword(String password, String deviceTag) {
        if (StringUtils.hasLength(password) && !passwordValidator.isValid(password, deviceTag)) {
            throw new InvalidPasswordException("Password does not meet the required criteria.");
        }
    }

    private void validateRefreshToken(String refreshToken) {
        if (!tokenService.isRefreshToken(refreshToken)) {
            throw new TokenForgeryException("Invalid refresh token.");
        }
    }

    private User createUser(String deviceTag, String password) {
        String encryptedPassword = encryptPassword(password);
        return User.create(deviceTag, encryptedPassword);
    }

    private String encryptDeviceTag(String deviceTag) {
        return encryptionUtils.encryptWithHashedIV(deviceTag, aesConfig.getDeviceTagSecretKey());
    }

    private String encryptPassword(String password) {
        return !StringUtils.hasLength(password) ? null : passwordEncoder.encode(password);
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
