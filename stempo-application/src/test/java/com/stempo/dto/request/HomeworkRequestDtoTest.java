package com.stempo.dto.request;

import static org.assertj.core.api.Assertions.assertThat;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class HomeworkRequestDtoTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void description이_없는_경우_유효성_검사에_실패한다() {
        // given
        HomeworkRequestDto homeworkRequestDto = new HomeworkRequestDto();
        homeworkRequestDto.setDescription(null);

        // when
        Set<ConstraintViolation<HomeworkRequestDto>> violations = validator.validate(homeworkRequestDto);

        // then
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).isEqualTo("Description is required");
    }

    @Test
    void description이_공백인_경우_유효성_검사에_실패한다() {
        // given
        HomeworkRequestDto homeworkRequestDto = new HomeworkRequestDto();
        homeworkRequestDto.setDescription("   ");

        // when
        Set<ConstraintViolation<HomeworkRequestDto>> violations = validator.validate(homeworkRequestDto);

        // then
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).isEqualTo("Description is required");
    }

    @Test
    void description이_정상적으로_있으면_유효성_검사에_성공한다() {
        // given
        HomeworkRequestDto homeworkRequestDto = new HomeworkRequestDto();
        homeworkRequestDto.setDescription("매일 스트레칭 운동 진행");

        // when
        Set<ConstraintViolation<HomeworkRequestDto>> violations = validator.validate(homeworkRequestDto);

        // then
        assertThat(violations).isEmpty();
    }
}
