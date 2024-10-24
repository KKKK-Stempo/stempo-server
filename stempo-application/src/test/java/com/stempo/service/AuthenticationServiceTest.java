package com.stempo.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.stempo.application.JwtTokenService;
import com.stempo.config.AesConfig;
import com.stempo.config.CustomAuthenticationProvider;
import com.stempo.dto.TokenInfo;
import com.stempo.dto.request.AuthRequestDto;
import com.stempo.model.Role;
import com.stempo.model.User;
import com.stempo.util.EncryptionUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

class AuthenticationServiceTest {

    @Mock
    private CustomAuthenticationProvider authenticationManager;

    @Mock
    private UserService userService;

    @Mock
    private EncryptionUtils encryptionUtils;

    @Mock
    private AesConfig aesConfig;

    @Mock
    private JwtTokenService tokenService;

    @Mock
    private TotpAuthenticatorService authenticatorService;

    @InjectMocks
    private AuthenticationService authenticationService;

    private AuthRequestDto authRequestDto;
    private String encryptedDeviceTag;
    private Authentication authentication;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        authRequestDto = new AuthRequestDto();
        authRequestDto.setDeviceTag("test-device");
        authRequestDto.setPassword("test-password");

        encryptedDeviceTag = "encrypted-user-device";

        when(aesConfig.getDeviceTagSecretKey()).thenReturn("test-secret-key");
        authentication = mock(Authentication.class);
    }

    @Test
    void 일반_사용자가_로그인에_성공하면_TokenInfo를_반환한다() {
        // given
        String deviceTag = "user-device";
        User user = User.builder().deviceTag(deviceTag).role(Role.USER).build();
        TokenInfo tokenInfo = TokenInfo.create("access-token", "refresh-token");

        when(encryptionUtils.encryptWithHashedIV(anyString(), anyString())).thenReturn(encryptedDeviceTag);
        when(userService.getById(encryptedDeviceTag)).thenReturn(user);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(authenticatorService.isAuthenticatorExist(encryptedDeviceTag)).thenReturn(false);
        when(tokenService.generateToken(authentication)).thenReturn(tokenInfo);

        // when
        Object result = authenticationService.login(authRequestDto, tokenService, authenticatorService);

        // then
        assertThat(result).isEqualTo(tokenInfo);
        verify(userService).resetFailedAttempts(encryptedDeviceTag);
    }

    @Test
    void 일반_사용자가_로그인에_실패하면_예외를_발생시킨다() {
        // given
        String deviceTag = "user-device";
        authRequestDto.setDeviceTag(deviceTag);

        when(encryptionUtils.encryptWithHashedIV(eq(deviceTag), anyString())).thenReturn(encryptedDeviceTag);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Invalid deviceTag or password."));

        // when, then
        assertThatThrownBy(() -> authenticationService.login(authRequestDto, tokenService, authenticatorService))
                .isInstanceOf(BadCredentialsException.class)
                .hasMessage("Invalid deviceTag or password.");

        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        verify(userService).handleFailedLogin(captor.capture());
        assertThat(captor.getValue()).isEqualTo(encryptedDeviceTag);
    }

    @Test
    void 관리자_로그인에_성공하고_TOTP가_존재하면_null을_반환한다() {
        // given
        String deviceTag = "admin-device";
        String encryptedDeviceTag = "encrypted-admin-device";
        User adminUser = User.builder().deviceTag(deviceTag).role(Role.ADMIN).build();

        when(encryptionUtils.encryptWithHashedIV(anyString(), anyString())).thenReturn(encryptedDeviceTag);
        when(userService.getById(encryptedDeviceTag)).thenReturn(adminUser);
        when(authenticatorService.isAuthenticatorExist(encryptedDeviceTag)).thenReturn(true);

        Authentication authentication = mock(Authentication.class);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);

        // when
        Object result = authenticationService.login(authRequestDto, tokenService, authenticatorService);

        // then
        assertThat(result).isNull();
        verify(userService).resetFailedAttempts(encryptedDeviceTag);
    }

    @Test
    void 관리자_로그인에_성공하고_TOTP가_존재하지_않으면_비밀키를_반환한다() {
        // given
        String deviceTag = "admin-device";
        String encryptedDeviceTag = "encrypted-admin-device";
        User adminUser = User.builder().deviceTag(deviceTag).role(Role.ADMIN).build();

        when(encryptionUtils.encryptWithHashedIV(anyString(), anyString())).thenReturn(encryptedDeviceTag);
        when(userService.getById(encryptedDeviceTag)).thenReturn(adminUser);
        when(authenticatorService.isAuthenticatorExist(encryptedDeviceTag)).thenReturn(false);
        when(authenticatorService.generateSecretKey(encryptedDeviceTag)).thenReturn("secret-key");

        Authentication authentication = mock(Authentication.class);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);

        // when
        Object result = authenticationService.login(authRequestDto, tokenService, authenticatorService);

        // then
        assertThat(result).isEqualTo("secret-key");
        verify(userService).resetFailedAttempts(encryptedDeviceTag);
        verify(authenticatorService).generateSecretKey(encryptedDeviceTag);
    }
}
