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

        handleDuplicateUser(deviceTag);
        validatePassword(password, deviceTag);

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
        validateLogin(deviceTag, requestDto.getPassword());

        return handleAuthentication(deviceTag, requestDto.getPassword());
    }

    @Override
    @Transactional
    public TokenInfo reissueToken(HttpServletRequest request) {
        String refreshToken = tokenService.resolveToken(request);
        validateRefreshToken(refreshToken);
        return reissueToken(refreshToken);
    }

    @Override
    @Transactional(noRollbackFor = {BadCredentialsException.class})
    public TokenInfo authenticate(TwoFactorAuthenticationRequestDto requestDto) {
        String deviceTag = encryptDeviceTag(requestDto.getDeviceTag());
        validateTwoFactorAuthentication(deviceTag, requestDto.getTotp());
        userService.resetFailedAttempts(deviceTag);
        Role role = userService.getById(deviceTag).getRole();
        return tokenService.generateToken(deviceTag, role);
    }

    @Override
    @Transactional
    public String resetAuthenticator(String deviceTag) {
        String encryptedDeviceTag = encryptDeviceTag(deviceTag);
        return authenticatorService.resetAuthenticator(encryptedDeviceTag);
    }

    private void handleDuplicateUser(String deviceTag) {
        if (userService.existsById(deviceTag)) {
            throw new UserAlreadyExistsException("User already exists.");
        }
    }

    private User createUser(String deviceTag, String password) {
        String encryptedPassword = encryptPassword(password);
        return User.create(deviceTag, encryptedPassword);
    }

    private Object handleAuthentication(String deviceTag, String password) {
        try {
            Authentication authentication = authenticateUser(deviceTag, password);
            userService.resetFailedAttempts(deviceTag);

            return handleTwoFactorAuthenticationIfNeeded(deviceTag, authentication);
        } catch (Exception e) {
            userService.handleFailedLogin(deviceTag);
            throw new BadCredentialsException("Invalid deviceTag or password.");
        }
    }

    private Authentication authenticateUser(String deviceTag, String password) {
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(deviceTag, password);
        return authenticationManager.authenticate(authenticationToken);
    }

    private Object handleTwoFactorAuthenticationIfNeeded(String deviceTag, Authentication authentication) {
        boolean isAdmin = userService.getById(deviceTag).isAdmin();
        boolean hasAuthenticator = authenticatorService.isAuthenticatorExist(deviceTag);

        if (isAdmin) {
            return hasAuthenticator ? null : authenticatorService.generateSecretKey(deviceTag);
        } else {
            return tokenService.generateToken(authentication);
        }
    }

    private void validateLogin(String deviceTag, String password) {
        userService.handleAccountLock(deviceTag);
        validatePassword(password, deviceTag);
    }

    private void validateTwoFactorAuthentication(String deviceTag, String totp) {
        userService.handleAccountLock(deviceTag);
        if (!authenticatorService.isAuthenticatorValid(deviceTag, totp)) {
            userService.handleFailedLogin(deviceTag);
            throw new BadCredentialsException("Invalid TOTP code.");
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
