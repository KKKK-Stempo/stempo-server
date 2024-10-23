package com.stempo.dto.request;

import static org.assertj.core.api.Assertions.assertThat;

import com.stempo.model.BoardCategory;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class BoardRequestDtoTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void 카테고리가_없는_경우_유효성_검사에_실패한다() {
        // given
        BoardRequestDto boardRequestDto = new BoardRequestDto();
        boardRequestDto.setCategory(null);
        boardRequestDto.setTitle("제목");
        boardRequestDto.setContent("내용");

        // when
        Set<ConstraintViolation<BoardRequestDto>> violations = validator.validate(boardRequestDto);

        // then
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).isEqualTo("Category is required");
    }

    @Test
    void 제목이_없는_경우_유효성_검사에_실패한다() {
        // given
        BoardRequestDto boardRequestDto = new BoardRequestDto();
        boardRequestDto.setCategory(BoardCategory.NOTICE);
        boardRequestDto.setTitle(null);
        boardRequestDto.setContent("내용");

        // when
        Set<ConstraintViolation<BoardRequestDto>> violations = validator.validate(boardRequestDto);

        // then
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).isEqualTo("Title is required");
    }

    @Test
    void 내용이_없는_경우_유효성_검사에_실패한다() {
        // given
        BoardRequestDto boardRequestDto = new BoardRequestDto();
        boardRequestDto.setCategory(BoardCategory.NOTICE);
        boardRequestDto.setTitle("제목");
        boardRequestDto.setContent(null);

        // when
        Set<ConstraintViolation<BoardRequestDto>> violations = validator.validate(boardRequestDto);

        // then
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).isEqualTo("Content is required");
    }

    @Test
    void 모든_필드가_정상적으로_있으면_유효성_검사에_성공한다() {
        // given
        BoardRequestDto boardRequestDto = new BoardRequestDto();
        boardRequestDto.setCategory(BoardCategory.NOTICE);
        boardRequestDto.setTitle("청각 자극을 통한 뇌성마비 환자 보행 패턴 개선 서비스, Stempo.");
        boardRequestDto.setContent("Stempo는 청각 자극을 통한 뇌성마비 환자 보행 패턴 개선 서비스입니다.");
        boardRequestDto.setFileUrls(List.of(
                "/resources/files/947051880039041_19dea234-b6ec-4c4b-bc92-c53c0d921943.wav",
                "/resources/files/boards/1/1030487120626166_1dec3611-c148-4139-bb16-3d2a89ac1dd7.pdf"
        ));

        // when
        Set<ConstraintViolation<BoardRequestDto>> violations = validator.validate(boardRequestDto);

        // then
        assertThat(violations).isEmpty();
    }
}
