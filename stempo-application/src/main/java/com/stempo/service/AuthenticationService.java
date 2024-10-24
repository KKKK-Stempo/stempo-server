package com.stempo.service;

import com.stempo.application.JwtTokenService;
import com.stempo.config.AesConfig;
import com.stempo.config.CustomAuthenticationProvider;
import com.stempo.dto.request.AuthRequestDto;
import com.stempo.util.EncryptionUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final CustomAuthenticationProvider authenticationManager;
    private final UserService userService;
    private final EncryptionUtils encryptionUtils;
    private final AesConfig aesConfig;

    @Transactional
    public Object login(AuthRequestDto requestDto, JwtTokenService tokenService,
            TotpAuthenticatorService authenticatorService) {
        String deviceTag = encryptDeviceTag(requestDto.getDeviceTag());
        userService.handleAccountLock(deviceTag);

        return handleAuthentication(deviceTag, requestDto.getPassword(), tokenService, authenticatorService);
    }

    private Object handleAuthentication(String deviceTag, String password, JwtTokenService tokenService,
            TotpAuthenticatorService authenticatorService) {
        try {
            Authentication authentication = authenticateUser(deviceTag, password);
            userService.resetFailedAttempts(deviceTag);

            return handleTwoFactorAuthenticationIfNeeded(deviceTag, authentication, authenticatorService, tokenService);
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

    private Object handleTwoFactorAuthenticationIfNeeded(String deviceTag, Authentication authentication,
            TotpAuthenticatorService authenticatorService, JwtTokenService tokenService) {
        boolean isAdmin = userService.getById(deviceTag).isAdmin();
        boolean hasAuthenticator = authenticatorService.isAuthenticatorExist(deviceTag);

        if (isAdmin) {
            return hasAuthenticator ? null : authenticatorService.generateSecretKey(deviceTag);
        } else {
            return tokenService.generateToken(authentication);
        }
    }

    private String encryptDeviceTag(String deviceTag) {
        return encryptionUtils.encryptWithHashedIV(deviceTag, aesConfig.getDeviceTagSecretKey());
    }
}
