package com.stempo.dto.request;

import static org.assertj.core.api.Assertions.assertThat;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TwoFactorAuthenticationRequestDtoTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void deviceTag이_null이면_유효성_검사에_실패한다() {
        TwoFactorAuthenticationRequestDto requestDto = new TwoFactorAuthenticationRequestDto();
        requestDto.setDeviceTag(null);
        requestDto.setTotp("123456");

        Set<ConstraintViolation<TwoFactorAuthenticationRequestDto>> violations = validator.validate(requestDto);

        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).isEqualTo("Device Tag is required");
    }

    @Test
    void totp가_null이면_유효성_검사에_실패한다() {
        TwoFactorAuthenticationRequestDto requestDto = new TwoFactorAuthenticationRequestDto();
        requestDto.setDeviceTag("490154203237518");
        requestDto.setTotp(null);

        Set<ConstraintViolation<TwoFactorAuthenticationRequestDto>> violations = validator.validate(requestDto);

        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).isEqualTo("TOTP is required");
    }

    @Test
    void 모든_필드가_유효하면_성공한다() {
        TwoFactorAuthenticationRequestDto requestDto = new TwoFactorAuthenticationRequestDto();
        requestDto.setDeviceTag("490154203237518");
        requestDto.setTotp("123456");

        Set<ConstraintViolation<TwoFactorAuthenticationRequestDto>> violations = validator.validate(requestDto);

        assertThat(violations).isEmpty();
    }
}
