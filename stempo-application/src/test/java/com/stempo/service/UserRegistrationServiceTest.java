package com.stempo.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.stempo.application.JwtTokenService;
import com.stempo.config.AesConfig;
import com.stempo.dto.TokenInfo;
import com.stempo.dto.request.AuthRequestDto;
import com.stempo.exception.InvalidPasswordException;
import com.stempo.exception.UserAlreadyExistsException;
import com.stempo.model.User;
import com.stempo.util.EncryptionUtils;
import com.stempo.util.PasswordValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class UserRegistrationServiceTest {

    @Mock
    private UserService userService;

    @Mock
    private PasswordValidator passwordValidator;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private EncryptionUtils encryptionUtils;

    @Mock
    private AesConfig aesConfig;

    @Mock
    private JwtTokenService tokenService;

    @InjectMocks
    private UserRegistrationService userRegistrationService;

    private AuthRequestDto authRequestDto;

    @BeforeEach
    void setUp() {
        authRequestDto = new AuthRequestDto();
        authRequestDto.setDeviceTag("test-device");
        authRequestDto.setPassword("Password123!");
        when(aesConfig.getDeviceTagSecretKey()).thenReturn("test-secret-key");
    }

    @Test
    void 사용자가_정상적으로_등록되면_TokenInfo를_반환한다() {
        // given
        String encryptedDeviceTag = "encrypted-device";
        TokenInfo tokenInfo = TokenInfo.create("access-token", "refresh-token");

        when(encryptionUtils.encryptWithHashedIV(anyString(), anyString())).thenReturn(encryptedDeviceTag);
        when(passwordValidator.isValid(anyString(), anyString())).thenReturn(true);
        when(passwordEncoder.encode(anyString())).thenReturn("encrypted-password");
        when(userService.existsById(anyString())).thenReturn(false);
        when(tokenService.generateToken(anyString(), any())).thenReturn(tokenInfo);

        // when
        TokenInfo result = userRegistrationService.registerUser(authRequestDto, tokenService);

        // then
        assertThat(result).isEqualTo(tokenInfo);
        verify(userService).save(any(User.class));
    }

    @Test
    void 중복_사용자일_경우_예외가_발생한다() {
        // given
        when(userService.existsById(any())).thenReturn(true);
        when(encryptionUtils.encryptWithHashedIV(any(), any())).thenReturn("encrypted-device");

        // when, then
        assertThatThrownBy(() -> userRegistrationService.registerUser(authRequestDto, tokenService))
                .isInstanceOf(UserAlreadyExistsException.class)
                .hasMessage("User already exists.");
    }

    @Test
    void 비밀번호가_유효하지_않을_경우_예외가_발생한다() {
        // when, then
        assertThatThrownBy(() -> userRegistrationService.registerUser(authRequestDto, tokenService))
                .isInstanceOf(InvalidPasswordException.class)
                .hasMessage("Password does not meet the required criteria.");
    }
}
