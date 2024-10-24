package com.stempo.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.stempo.application.JwtTokenService;
import com.stempo.config.AesConfig;
import com.stempo.dto.TokenInfo;
import com.stempo.dto.request.TwoFactorAuthenticationRequestDto;
import com.stempo.model.Role;
import com.stempo.model.User;
import com.stempo.util.EncryptionUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.BadCredentialsException;

class TotpServiceTest {

    @Mock
    private TotpAuthenticatorService authenticatorService;

    @Mock
    private UserService userService;

    @Mock
    private EncryptionUtils encryptionUtils;

    @Mock
    private AesConfig aesConfig;

    @Mock
    private JwtTokenService tokenService;

    @InjectMocks
    private TotpService totpService;

    private TwoFactorAuthenticationRequestDto requestDto;
    private User user;
    private TokenInfo tokenInfo;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        requestDto = new TwoFactorAuthenticationRequestDto();
        requestDto.setDeviceTag("test-device");
        requestDto.setTotp("123456");

        user = User.builder().deviceTag("test-device").role(Role.USER).build();
        tokenInfo = TokenInfo.create("access-token", "refresh-token");

        when(aesConfig.getDeviceTagSecretKey()).thenReturn("test-secret-key");
    }

    @Test
    void TOTP_인증이_성공하면_TokenInfo를_반환한다() {
        // given
        String encryptedDeviceTag = "encrypted-test-device";
        when(encryptionUtils.encryptWithHashedIV(anyString(), anyString())).thenReturn(encryptedDeviceTag);
        when(authenticatorService.isAuthenticatorValid(encryptedDeviceTag, requestDto.getTotp())).thenReturn(true);
        when(userService.getById(encryptedDeviceTag)).thenReturn(user);
        when(tokenService.generateToken(encryptedDeviceTag, Role.USER)).thenReturn(tokenInfo);

        // when
        TokenInfo result = totpService.authenticate(requestDto, tokenService);

        // then
        assertThat(result).isEqualTo(tokenInfo);
        verify(userService).resetFailedAttempts(encryptedDeviceTag);
        verify(authenticatorService).isAuthenticatorValid(encryptedDeviceTag, requestDto.getTotp());
        verify(tokenService).generateToken(encryptedDeviceTag, Role.USER);
    }

    @Test
    void TOTP_인증이_실패하면_예외를_발생시킨다() {
        // given
        String encryptedDeviceTag = "encrypted-test-device";
        when(encryptionUtils.encryptWithHashedIV(anyString(), anyString())).thenReturn(encryptedDeviceTag);
        when(authenticatorService.isAuthenticatorValid(encryptedDeviceTag, requestDto.getTotp())).thenReturn(false);

        // when & then
        assertThatThrownBy(() -> totpService.authenticate(requestDto, tokenService))
                .isInstanceOf(BadCredentialsException.class)
                .hasMessage("Invalid TOTP code.");

        verify(userService).handleAccountLock(encryptedDeviceTag);
        verify(authenticatorService).isAuthenticatorValid(encryptedDeviceTag, requestDto.getTotp());
        verify(userService).handleFailedLogin(encryptedDeviceTag);
        verify(tokenService, never()).generateToken(anyString(), any());
    }

    @Test
    void 시크릿키를_리셋하면_새로운_시크릿키를_반환한다() {
        // given
        String deviceTag = "test-device";
        String encryptedDeviceTag = "encrypted-test-device";
        when(encryptionUtils.encryptWithHashedIV(anyString(), anyString())).thenReturn(encryptedDeviceTag);
        when(authenticatorService.resetAuthenticator(encryptedDeviceTag)).thenReturn("new-secret-key");

        // when
        String result = totpService.resetAuthenticator(deviceTag);

        // then
        assertThat(result).isEqualTo("new-secret-key");
        verify(encryptionUtils).encryptWithHashedIV(deviceTag, "test-secret-key");
        verify(authenticatorService).resetAuthenticator(encryptedDeviceTag);
    }
}
