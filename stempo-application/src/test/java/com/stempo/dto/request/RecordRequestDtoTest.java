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

    // 모든 필드가 유효한 기본값으로 설정된 인스턴스를 생성하는 헬퍼 메서드
    private RecordRequestDto createValidDto() {
        RecordRequestDto dto = new RecordRequestDto();
        dto.setAccuracy(50.0);         // 0 ~ 100 사이의 유효한 값
        dto.setDuration(60);           // 0 이상의 유효한 값
        dto.setSteps(100);             // 0 이상의 유효한 값
        dto.setLeftFootAvgSpeed(1.0);  // 0 이상의 유효한 값
        dto.setRightFootAvgSpeed(1.0); // 0 이상의 유효한 값
        dto.setBit(4);                 // 1 ~ 8 사이의 유효한 값
        dto.setBpm(60);                // 10 ~ 200 사이의 유효한 값
        return dto;
    }

    @Test
    void accuracy가_null이면_유효성_검사에_실패한다() {
        // given
        RecordRequestDto recordRequestDto = createValidDto();
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
        RecordRequestDto recordRequestDto = createValidDto();
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
        RecordRequestDto recordRequestDto = createValidDto();
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
        RecordRequestDto recordRequestDto = createValidDto();
        recordRequestDto.setAccuracy(85.5);

        // when
        Set<ConstraintViolation<RecordRequestDto>> violations = validator.validate(recordRequestDto);

        // then
        assertThat(violations).isEmpty();
    }

    @Test
    void duration이_음수면_유효성_검사에_실패한다() {
        // given
        RecordRequestDto recordRequestDto = createValidDto();
        recordRequestDto.setDuration(-10);

        // when
        Set<ConstraintViolation<RecordRequestDto>> violations = validator.validate(recordRequestDto);

        // then
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage())
            .isEqualTo("Duration must be a positive value or zero");
    }

    @Test
    void steps가_음수면_유효성_검사에_실패한다() {
        // given
        RecordRequestDto recordRequestDto = createValidDto();
        recordRequestDto.setSteps(-5);

        // when
        Set<ConstraintViolation<RecordRequestDto>> violations = validator.validate(recordRequestDto);

        // then
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage())
            .isEqualTo("Steps must be a positive value or zero");
    }

    @Test
    void duration과_steps가_정상_범위_내_값이면_유효성_검사에_성공한다() {
        // given
        RecordRequestDto recordRequestDto = createValidDto();
        recordRequestDto.setDuration(60);
        recordRequestDto.setSteps(500);

        // when
        Set<ConstraintViolation<RecordRequestDto>> violations = validator.validate(recordRequestDto);

        // then
        assertThat(violations).isEmpty();
    }

    @Test
    void bpm이_null이면_유효성_검사에_실패한다() {
        // given
        RecordRequestDto recordRequestDto = createValidDto();
        recordRequestDto.setBpm(null);

        // when
        Set<ConstraintViolation<RecordRequestDto>> violations = validator.validate(recordRequestDto);

        // then
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).isEqualTo("BPM is required");
    }

    @Test
    void bpm이_범위를_벗어나면_유효성_검사에_실패한다() {
        // given
        RecordRequestDto recordRequestDto = createValidDto();
        recordRequestDto.setBpm(5);

        // when
        Set<ConstraintViolation<RecordRequestDto>> violations = validator.validate(recordRequestDto);

        // then
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage())
            .isEqualTo("BPM must be between 10 and 200");
    }

    @Test
    void bit이_null이면_유효성_검사에_실패한다() {
        // given
        RecordRequestDto recordRequestDto = createValidDto();
        recordRequestDto.setBit(null);

        // when
        Set<ConstraintViolation<RecordRequestDto>> violations = validator.validate(recordRequestDto);

        // then
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).isEqualTo("Bit is required");
    }

    @Test
    void bit이_범위를_벗어나면_유효성_검사에_실패한다() {
        // given
        RecordRequestDto recordRequestDto = createValidDto();
        recordRequestDto.setBit(9);

        // when
        Set<ConstraintViolation<RecordRequestDto>> violations = validator.validate(recordRequestDto);

        // then
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage())
            .isEqualTo("Bit must be between 1 and 8");
    }

    @Test
    void leftFootAvgSpeed이_음수면_유효성_검사에_실패한다() {
        // given
        RecordRequestDto recordRequestDto = createValidDto();
        recordRequestDto.setLeftFootAvgSpeed(-0.1);

        // when
        Set<ConstraintViolation<RecordRequestDto>> violations = validator.validate(recordRequestDto);

        // then
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage())
            .isEqualTo("Left foot average speed must be a positive value or zero");
    }

    @Test
    void rightFootAvgSpeed이_음수면_유효성_검사에_실패한다() {
        // given
        RecordRequestDto recordRequestDto = createValidDto();
        recordRequestDto.setRightFootAvgSpeed(-0.1);

        // when
        Set<ConstraintViolation<RecordRequestDto>> violations = validator.validate(recordRequestDto);

        // then
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage())
            .isEqualTo("Right foot average speed must be a positive value or zero");
    }
}
