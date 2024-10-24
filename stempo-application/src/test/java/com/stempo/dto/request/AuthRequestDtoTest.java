package com.stempo.dto.request;

import static org.assertj.core.api.Assertions.assertThat;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class AuthRequestDtoTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void deviceTag가_null이면_유효성_검사에_실패한다() {
        // given
        AuthRequestDto authRequestDto = new AuthRequestDto();
        authRequestDto.setDeviceTag(null);
        authRequestDto.setPassword("password");

        // when
        Set<ConstraintViolation<AuthRequestDto>> violations = validator.validate(authRequestDto);

        // then
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).isEqualTo("DeviceTag is required");
    }

    @Test
    void deviceTag가_빈_문자열이면_유효성_검사에_실패한다() {
        // given
        AuthRequestDto authRequestDto = new AuthRequestDto();
        authRequestDto.setDeviceTag("");
        authRequestDto.setPassword("password");

        // when
        Set<ConstraintViolation<AuthRequestDto>> violations = validator.validate(authRequestDto);

        // then
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).isEqualTo("DeviceTag is required");
    }

    @Test
    void deviceTag가_공백이면_유효성_검사에_실패한다() {
        // given
        AuthRequestDto authRequestDto = new AuthRequestDto();
        authRequestDto.setDeviceTag("   ");
        authRequestDto.setPassword("password");

        // when
        Set<ConstraintViolation<AuthRequestDto>> violations = validator.validate(authRequestDto);

        // then
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).isEqualTo("DeviceTag is required");
    }

    @Test
    void deviceTag가_정상적으로_있으면_유효성_검사에_성공한다() {
        // given
        AuthRequestDto authRequestDto = new AuthRequestDto();
        authRequestDto.setDeviceTag("490154203237518");
        authRequestDto.setPassword("password");

        // when
        Set<ConstraintViolation<AuthRequestDto>> violations = validator.validate(authRequestDto);

        // then
        assertThat(violations).isEmpty();
    }
}
