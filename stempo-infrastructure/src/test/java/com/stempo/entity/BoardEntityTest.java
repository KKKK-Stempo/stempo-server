package com.stempo.entity;

import static org.assertj.core.api.Assertions.assertThat;

import com.stempo.model.BoardCategory;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class BoardEntityTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void 제목이_100자를_초과하면_예외가_발생한다() {
        // given
        BoardEntity boardEntity = BoardEntity.builder()
                .title("a".repeat(101))
                .deviceTag("device123")
                .category(BoardCategory.NOTICE)
                .content("Some content")
                .build();

        // when
        Set<ConstraintViolation<BoardEntity>> violations = validator.validate(boardEntity);

        // then
        assertThat(violations).isNotEmpty();
        assertThat(violations)
                .anyMatch(violation -> violation.getPropertyPath().toString().equals("title"));
    }

    @Test
    void 제목이_빈값이면_예외가_발생한다() {
        // given
        BoardEntity boardEntity = BoardEntity.builder()
                .title("")
                .deviceTag("device123")
                .category(BoardCategory.NOTICE)
                .content("Some content")
                .build();

        // when
        Set<ConstraintViolation<BoardEntity>> violations = validator.validate(boardEntity);

        // then
        assertThat(violations).isNotEmpty();
        assertThat(violations)
                .anyMatch(violation -> violation.getPropertyPath().toString().equals("title"));
    }

    @Test
    void 내용이_10000자를_초과하면_예외가_발생한다() {
        // given
        BoardEntity boardEntity = BoardEntity.builder()
                .title("Valid Title")
                .deviceTag("device123")
                .category(BoardCategory.NOTICE)
                .content("a".repeat(10001))
                .build();

        // when
        Set<ConstraintViolation<BoardEntity>> violations = validator.validate(boardEntity);

        // then
        assertThat(violations).isNotEmpty();
        assertThat(violations)
                .anyMatch(violation -> violation.getPropertyPath().toString().equals("content"));
    }

    @Test
    void 내용이_빈값이면_예외가_발생한다() {
        // given
        BoardEntity boardEntity = BoardEntity.builder()
                .title("Valid Title")
                .deviceTag("device123")
                .category(BoardCategory.NOTICE)
                .content("")
                .build();

        // when
        Set<ConstraintViolation<BoardEntity>> violations = validator.validate(boardEntity);

        // then
        assertThat(violations).isNotEmpty();
        assertThat(violations)
                .anyMatch(violation -> violation.getPropertyPath().toString().equals("content"));
    }

    @Test
    void 정상적인_데이터는_유효성_검사를_통과한다() {
        // given
        BoardEntity boardEntity = BoardEntity.builder()
                .title("Valid Title")
                .deviceTag("device123")
                .category(BoardCategory.NOTICE)
                .content("Valid content")
                .build();

        // when
        Set<ConstraintViolation<BoardEntity>> violations = validator.validate(boardEntity);

        // then
        assertThat(violations).isEmpty();
    }
}
