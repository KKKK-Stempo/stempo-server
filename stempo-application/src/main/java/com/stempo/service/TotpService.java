package com.stempo.service;

import com.stempo.application.JwtTokenService;
import com.stempo.config.AesConfig;
import com.stempo.dto.TokenInfo;
import com.stempo.dto.request.TwoFactorAuthenticationRequestDto;
import com.stempo.model.Role;
import com.stempo.util.EncryptionUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TotpService {

    private final TotpAuthenticatorService authenticatorService;
    private final UserService userService;
    private final EncryptionUtils encryptionUtils;
    private final AesConfig aesConfig;

    @Transactional
    public TokenInfo authenticate(TwoFactorAuthenticationRequestDto requestDto, JwtTokenService tokenService) {
        String deviceTag = encryptDeviceTag(requestDto.getDeviceTag());
        validateTwoFactorAuthentication(deviceTag, requestDto.getTotp());
        userService.resetFailedAttempts(deviceTag);
        Role role = userService.getById(deviceTag).getRole();
        return tokenService.generateToken(deviceTag, role);
    }

    @Transactional
    public String resetAuthenticator(String deviceTag) {
        String encryptedDeviceTag = encryptDeviceTag(deviceTag);
        return authenticatorService.resetAuthenticator(encryptedDeviceTag);
    }

    private void validateTwoFactorAuthentication(String deviceTag, String totp) {
        userService.handleAccountLock(deviceTag);
        if (!authenticatorService.isAuthenticatorValid(deviceTag, totp)) {
            userService.handleFailedLogin(deviceTag);
            throw new BadCredentialsException("Invalid TOTP code.");
        }
    }

    private String encryptDeviceTag(String deviceTag) {
        return encryptionUtils.encryptWithHashedIV(deviceTag, aesConfig.getDeviceTagSecretKey());
    }
}
