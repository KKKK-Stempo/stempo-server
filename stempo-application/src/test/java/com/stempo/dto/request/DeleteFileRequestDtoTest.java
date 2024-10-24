package com.stempo.dto.request;

import static org.assertj.core.api.Assertions.assertThat;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class DeleteFileRequestDtoTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void url이_없는_경우_유효성_검사에_실패한다() {
        // given
        DeleteFileRequestDto deleteFileRequestDto = new DeleteFileRequestDto();
        deleteFileRequestDto.setUrl(null);

        // when
        Set<ConstraintViolation<DeleteFileRequestDto>> violations = validator.validate(deleteFileRequestDto);

        // then
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).isEqualTo("File URL is required");
    }

    @Test
    void url이_공백인_경우_유효성_검사에_실패한다() {
        // given
        DeleteFileRequestDto deleteFileRequestDto = new DeleteFileRequestDto();
        deleteFileRequestDto.setUrl("   ");

        // when
        Set<ConstraintViolation<DeleteFileRequestDto>> violations = validator.validate(deleteFileRequestDto);

        // then
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).isEqualTo("File URL is required");
    }

    @Test
    void url이_정상적으로_있으면_유효성_검사에_성공한다() {
        // given
        DeleteFileRequestDto deleteFileRequestDto = new DeleteFileRequestDto();
        deleteFileRequestDto.setUrl("/resources/files/947051880039041_19dea234-b6ec-4c4b-bc92-c53c0d921943.jpg");

        // when
        Set<ConstraintViolation<DeleteFileRequestDto>> violations = validator.validate(deleteFileRequestDto);

        // then
        assertThat(violations).isEmpty();
    }
}
