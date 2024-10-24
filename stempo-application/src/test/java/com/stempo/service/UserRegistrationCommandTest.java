package com.stempo.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import com.stempo.application.JwtTokenService;
import com.stempo.dto.TokenInfo;
import com.stempo.dto.request.AuthRequestDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class UserRegistrationCommandTest {

    @Mock
    private UserRegistrationService userRegistrationService;

    @Mock
    private JwtTokenService jwtTokenService;

    private AuthRequestDto requestDto;

    private UserRegistrationCommand userRegistrationCommand;

    private TokenInfo tokenInfo;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        requestDto = new AuthRequestDto();
        requestDto.setDeviceTag("test-device-tag");
        requestDto.setPassword("test-password");

        tokenInfo = TokenInfo.create("access-token", "refresh-token");

        userRegistrationCommand = new UserRegistrationCommand(userRegistrationService, requestDto, jwtTokenService);
    }

    @Test
    void execute_메서드가_토큰정보를_반환한다() {
        // given
        when(userRegistrationService.registerUser(requestDto, jwtTokenService)).thenReturn(tokenInfo);

        // when
        TokenInfo result = userRegistrationCommand.execute();

        // then
        assertThat(result).isEqualTo(tokenInfo);
        verify(userRegistrationService).registerUser(requestDto, jwtTokenService);
        verifyNoMoreInteractions(userRegistrationService, jwtTokenService);
    }
}
