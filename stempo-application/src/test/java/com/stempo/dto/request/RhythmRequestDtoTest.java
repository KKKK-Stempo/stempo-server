package com.stempo.dto.request;

import static org.assertj.core.api.Assertions.assertThat;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class RhythmRequestDtoTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void bpm이_null이면_유효성_검사에_실패한다() {
        RhythmRequestDto rhythmRequestDto = new RhythmRequestDto();
        rhythmRequestDto.setBpm(null);
        rhythmRequestDto.setBit(4);

        Set<ConstraintViolation<RhythmRequestDto>> violations = validator.validate(rhythmRequestDto);

        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).isEqualTo("BPM is required");
    }

    @Test
    void bpm이_범위_밖이면_유효성_검사에_실패한다() {
        RhythmRequestDto rhythmRequestDto = new RhythmRequestDto();
        rhythmRequestDto.setBpm(250);
        rhythmRequestDto.setBit(4);

        Set<ConstraintViolation<RhythmRequestDto>> violations = validator.validate(rhythmRequestDto);

        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).isEqualTo("BPM must be between 10 and 200");
    }

    @Test
    void bit이_null이면_유효성_검사에_실패한다() {
        RhythmRequestDto rhythmRequestDto = new RhythmRequestDto();
        rhythmRequestDto.setBpm(60);
        rhythmRequestDto.setBit(null);

        Set<ConstraintViolation<RhythmRequestDto>> violations = validator.validate(rhythmRequestDto);

        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).isEqualTo("Bit is required");
    }

    @Test
    void bit이_범위_밖이면_유효성_검사에_실패한다() {
        RhythmRequestDto rhythmRequestDto = new RhythmRequestDto();
        rhythmRequestDto.setBpm(60);
        rhythmRequestDto.setBit(10);

        Set<ConstraintViolation<RhythmRequestDto>> violations = validator.validate(rhythmRequestDto);

        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).isEqualTo("Bit must be between 1 and 8");
    }

    @Test
    void 모든_필드가_유효하면_성공한다() {
        RhythmRequestDto rhythmRequestDto = new RhythmRequestDto();
        rhythmRequestDto.setBpm(120);
        rhythmRequestDto.setBit(4);

        Set<ConstraintViolation<RhythmRequestDto>> violations = validator.validate(rhythmRequestDto);

        assertThat(violations).isEmpty();
    }
}
