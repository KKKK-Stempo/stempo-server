package com.stempo.service;

import com.stempo.application.JwtTokenService;
import com.stempo.dto.TokenInfo;
import com.stempo.dto.request.TwoFactorAuthenticationRequestDto;
import com.stempo.exception.BaseException;
import com.stempo.exception.ErrorCode;
import com.stempo.model.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TotpService {

    private final TotpAuthenticatorService authenticatorService;
    private final UserService userService;

    @Transactional
    public TokenInfo authenticate(TwoFactorAuthenticationRequestDto requestDto, JwtTokenService tokenService) {
        String deviceTag = userService.encryptDeviceTag(requestDto.getDeviceTag());
        validateTwoFactorAuthentication(deviceTag, requestDto.getTotp());
        userService.resetFailedAttempts(deviceTag);
        Role role = userService.getById(deviceTag).getRole();
        return tokenService.generateToken(deviceTag, role);
    }

    @Transactional
    public String resetAuthenticator(String deviceTag) {
        String encryptedDeviceTag = userService.encryptDeviceTag(deviceTag);
        return authenticatorService.resetAuthenticator(encryptedDeviceTag);
    }

    private void validateTwoFactorAuthentication(String deviceTag, String totp) {
        userService.handleAccountLock(deviceTag);
        if (!authenticatorService.isAuthenticatorValid(deviceTag, totp)) {
            userService.handleFailedLogin(deviceTag);
            throw new BaseException(ErrorCode.BAD_CREDENTIALS, "잘못된 TOTP 코드입니다.");
        }
    }
}
