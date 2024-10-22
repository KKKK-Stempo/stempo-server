package com.stempo.dto;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class ErrorResponseTest {

    @Test
    void 에러_응답을_생성할_수_있다() {
        // given
        Exception exception = new NullPointerException();

        // when
        ErrorResponse<Void> response = ErrorResponse.failure(exception);

        // then
        assertThat(response.getSuccess()).isFalse();
        assertThat(response.getErrorMessage()).isEqualTo("NullPointerException");
        assertThat(response.getData()).isNull();
    }

    @Test
    void 에러_응답을_JSON_형태로_변환할_수_있다() {
        // given
        Exception exception = new NullPointerException();
        ErrorResponse<Void> response = ErrorResponse.failure(exception);

        // when
        String json = response.toJson();

        // then
        String expectedJson = "{\"success\":false,\"data\":null,\"errorMessage\":\"NullPointerException\"}";
        assertThat(json).isEqualTo(expectedJson);
    }
}
