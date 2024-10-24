package com.stempo.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.stempo.application.JwtTokenService;
import com.stempo.dto.TokenInfo;
import com.stempo.dto.request.AuthRequestDto;
import com.stempo.dto.request.TwoFactorAuthenticationRequestDto;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.BadCredentialsException;

class AuthServiceImplTest {

    @Mock
    private UserRegistrationService userRegistrationService;

    @Mock
    private AuthenticationService authenticationService;

    @Mock
    private TotpService totpService;

    @Mock
    private TokenService tokenService;

    @Mock
    private UserEventService userEventService;

    @Mock
    private JwtTokenService jwtTokenService;

    @Mock
    private TotpAuthenticatorService totpAuthenticatorService;

    @InjectMocks
    private AuthServiceImpl authServiceImpl;

    private AuthRequestDto authRequestDto;
    private TwoFactorAuthenticationRequestDto twoFactorAuthenticationRequestDto;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        authRequestDto = new AuthRequestDto();
        authRequestDto.setDeviceTag("test-device");
        authRequestDto.setPassword("test-password");

        twoFactorAuthenticationRequestDto = new TwoFactorAuthenticationRequestDto();
        twoFactorAuthenticationRequestDto.setDeviceTag("test-device");
        twoFactorAuthenticationRequestDto.setTotp("123456");
    }

    @Test
    void 사용자_등록_성공시_TokenInfo를_반환한다() {
        // given
        TokenInfo expectedTokenInfo = TokenInfo.create("access-token", "refresh-token");
        when(userRegistrationService.registerUser(any(AuthRequestDto.class), any(JwtTokenService.class)))
                .thenReturn(expectedTokenInfo);

        // when
        TokenInfo result = authServiceImpl.registerUser(authRequestDto);

        // then
        assertThat(result).isEqualTo(expectedTokenInfo);
        verify(userRegistrationService).registerUser(any(AuthRequestDto.class), any(JwtTokenService.class));
    }

    @Test
    void 사용자_탈퇴시_deviceTag를_반환한다() {
        // given
        String expectedDeviceTag = "test-device";
        when(userEventService.unregisterUser()).thenReturn(expectedDeviceTag);

        // when
        String result = authServiceImpl.unregisterUser();

        // then
        assertThat(result).isEqualTo(expectedDeviceTag);
        verify(userEventService).unregisterUser();
    }

    @Test
    void 로그인_성공시_TokenInfo를_반환한다() {
        // given
        TokenInfo expectedTokenInfo = TokenInfo.create("access-token", "refresh-token");
        when(authenticationService.login(any(AuthRequestDto.class), any(JwtTokenService.class),
                any(TotpAuthenticatorService.class)))
                .thenReturn(expectedTokenInfo);

        // when
        Object result = authServiceImpl.login(authRequestDto);

        // then
        assertThat(result).isEqualTo(expectedTokenInfo);
        verify(authenticationService).login(any(AuthRequestDto.class), any(JwtTokenService.class),
                any(TotpAuthenticatorService.class));
    }

    @Test
    void 로그인_실패시_BadCredentialsException을_발생시킨다() {
        // given
        when(authenticationService.login(any(AuthRequestDto.class), any(JwtTokenService.class),
                any(TotpAuthenticatorService.class)))
                .thenThrow(new BadCredentialsException("Invalid credentials"));

        // when, then
        assertThatThrownBy(() -> authServiceImpl.login(authRequestDto))
                .isInstanceOf(BadCredentialsException.class)
                .hasMessage("Invalid credentials");
        verify(authenticationService).login(any(AuthRequestDto.class), any(JwtTokenService.class),
                any(TotpAuthenticatorService.class));
    }

    @Test
    void 토큰_재발급시_TokenInfo를_반환한다() {
        // given
        HttpServletRequest request = mock(HttpServletRequest.class);
        TokenInfo expectedTokenInfo = TokenInfo.create("access-token", "refresh-token");
        when(tokenService.reissueToken(any(HttpServletRequest.class))).thenReturn(expectedTokenInfo);

        // when
        TokenInfo result = authServiceImpl.reissueToken(request);

        // then
        assertThat(result).isEqualTo(expectedTokenInfo);
        verify(tokenService).reissueToken(any(HttpServletRequest.class));
    }

    @Test
    void _2단계_인증_성공시_TokenInfo를_반환한다() {
        // given
        TokenInfo expectedTokenInfo = TokenInfo.create("access-token", "refresh-token");
        when(totpService.authenticate(any(TwoFactorAuthenticationRequestDto.class), any(JwtTokenService.class)))
                .thenReturn(expectedTokenInfo);

        // when
        TokenInfo result = authServiceImpl.authenticate(twoFactorAuthenticationRequestDto);

        // then
        assertThat(result).isEqualTo(expectedTokenInfo);
        verify(totpService).authenticate(any(TwoFactorAuthenticationRequestDto.class), any(JwtTokenService.class));
    }

    @Test
    void _2단계_인증_실패시_BadCredentialsException을_발생시킨다() {
        // given
        when(totpService.authenticate(any(TwoFactorAuthenticationRequestDto.class), any(JwtTokenService.class)))
                .thenThrow(new BadCredentialsException("Invalid credentials"));

        // when, then
        assertThatThrownBy(() -> authServiceImpl.authenticate(twoFactorAuthenticationRequestDto))
                .isInstanceOf(BadCredentialsException.class)
                .hasMessage("Invalid credentials");
        verify(totpService).authenticate(any(TwoFactorAuthenticationRequestDto.class), any(JwtTokenService.class));
    }

    @Test
    void 인증기_재설정시_secretKey를_반환한다() {
        // given
        String deviceTag = "test-device";
        String expectedSecretKey = "secret-key";
        when(totpService.resetAuthenticator(anyString())).thenReturn(expectedSecretKey);

        // when
        String result = authServiceImpl.resetAuthenticator(deviceTag);

        // then
        assertThat(result).isEqualTo(expectedSecretKey);
        verify(totpService).resetAuthenticator(anyString());
    }
}
