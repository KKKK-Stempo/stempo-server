package com.stempo.dto.request;

import static org.assertj.core.api.Assertions.assertThat;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class RecordRequestDtoTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void accuracy가_null이면_유효성_검사에_실패한다() {
        // given
        RecordRequestDto recordRequestDto = new RecordRequestDto();
        recordRequestDto.setAccuracy(null);

        // when
        Set<ConstraintViolation<RecordRequestDto>> violations = validator.validate(recordRequestDto);

        // then
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).isEqualTo("Accuracy is required");
    }

    @Test
    void accuracy가_음수면_유효성_검사에_실패한다() {
        // given
        RecordRequestDto recordRequestDto = new RecordRequestDto();
        recordRequestDto.setAccuracy(-1.0);

        // when
        Set<ConstraintViolation<RecordRequestDto>> violations = validator.validate(recordRequestDto);

        // then
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).isEqualTo("Accuracy must be between 0 and 100");
    }

    @Test
    void accuracy가_100을_초과하면_유효성_검사에_실패한다() {
        // given
        RecordRequestDto recordRequestDto = new RecordRequestDto();
        recordRequestDto.setAccuracy(101.0);

        // when
        Set<ConstraintViolation<RecordRequestDto>> violations = validator.validate(recordRequestDto);

        // then
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).isEqualTo("Accuracy must be between 0 and 100");
    }

    @Test
    void accuracy가_정상_범위_내_값이면_유효성_검사에_성공한다() {
        // given
        RecordRequestDto recordRequestDto = new RecordRequestDto();
        recordRequestDto.setAccuracy(85.5);

        // when
        Set<ConstraintViolation<RecordRequestDto>> violations = validator.validate(recordRequestDto);

        // then
        assertThat(violations).isEmpty();
    }

    @Test
    void duration이_음수면_유효성_검사에_실패한다() {
        // given
        RecordRequestDto recordRequestDto = new RecordRequestDto();
        recordRequestDto.setAccuracy(50.0);
        recordRequestDto.setDuration(-10);

        // when
        Set<ConstraintViolation<RecordRequestDto>> violations = validator.validate(recordRequestDto);

        // then
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).isEqualTo("Duration must be a positive value or zero");
    }

    @Test
    void steps가_음수면_유효성_검사에_실패한다() {
        // given
        RecordRequestDto recordRequestDto = new RecordRequestDto();
        recordRequestDto.setAccuracy(50.0);
        recordRequestDto.setSteps(-5);

        // when
        Set<ConstraintViolation<RecordRequestDto>> violations = validator.validate(recordRequestDto);

        // then
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).isEqualTo("Steps must be a positive value or zero");
    }

    @Test
    void duration과_steps가_정상_범위_내_값이면_유효성_검사에_성공한다() {
        // given
        RecordRequestDto recordRequestDto = new RecordRequestDto();
        recordRequestDto.setAccuracy(95.0);
        recordRequestDto.setDuration(60);
        recordRequestDto.setSteps(500);

        // when
        Set<ConstraintViolation<RecordRequestDto>> violations = validator.validate(recordRequestDto);

        // then
        assertThat(violations).isEmpty();
    }
}
