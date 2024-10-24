package com.stempo.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import com.stempo.application.JwtTokenService;
import com.stempo.dto.request.AuthRequestDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class LoginCommandTest {

    @Mock
    private AuthenticationService authenticationService;

    @Mock
    private JwtTokenService jwtTokenService;

    @Mock
    private TotpAuthenticatorService totpAuthenticatorService;

    private AuthRequestDto requestDto;

    private LoginCommand loginCommand;

    private Object expectedResponse;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        requestDto = new AuthRequestDto();
        requestDto.setDeviceTag("test-device-tag");
        requestDto.setPassword("test-password");

        expectedResponse = new Object();

        loginCommand = new LoginCommand(authenticationService, requestDto, jwtTokenService, totpAuthenticatorService);
    }

    @Test
    void execute_메서드가_로그인_결과를_반환한다() {
        // given
        when(authenticationService.login(requestDto, jwtTokenService, totpAuthenticatorService))
                .thenReturn(expectedResponse);

        // when
        Object result = loginCommand.execute();

        // then
        assertThat(result).isEqualTo(expectedResponse);
        verify(authenticationService).login(requestDto, jwtTokenService, totpAuthenticatorService);
        verifyNoMoreInteractions(authenticationService, jwtTokenService, totpAuthenticatorService);
    }
}
