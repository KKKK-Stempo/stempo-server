package com.stempo.dto;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class ApiResponseTest {

    @Test
    void 성공_응답을_생성할_수_있다() {
        // when
        ApiResponse<String> response = ApiResponse.success("Test data");

        // then
        assertThat(response.getSuccess()).isTrue();
        assertThat(response.getData()).isEqualTo("Test data");
    }

    @Test
    void 실패_응답을_생성할_수_있다() {
        // when
        ApiResponse<String> response = ApiResponse.failure("Error data");

        // then
        assertThat(response.getSuccess()).isFalse();
        assertThat(response.getData()).isEqualTo("Error data");
    }

    @Test
    void 성공_응답_빈_데이터를_생성할_수_있다() {
        // when
        ApiResponse<Void> response = ApiResponse.success();

        // then
        assertThat(response.getSuccess()).isTrue();
        assertThat(response.getData()).isNull();
    }

    @Test
    void 실패_응답_빈_데이터를_생성할_수_있다() {
        // when
        ApiResponse<Void> response = ApiResponse.failure();

        // then
        assertThat(response.getSuccess()).isFalse();
        assertThat(response.getData()).isNull();
    }

    @Test
    void 응답을_JSON_형태로_변환할_수_있다() {
        // given
        ApiResponse<String> response = ApiResponse.success("Test data");

        // when
        String json = response.toJson();

        // then
        assertThat(json).isEqualTo("{\"success\":true,\"data\":\"Test data\"}");
    }

    @Test
    void 실패_응답을_JSON_형태로_변환할_수_있다() {
        // given
        ApiResponse<String> response = ApiResponse.failure("Error data");

        // when
        String json = response.toJson();

        // then
        assertThat(json).isEqualTo("{\"success\":false,\"data\":\"Error data\"}");
    }

    @Test
    void 빈_데이터_응답을_JSON_형태로_변환할_수_있다() {
        // given
        ApiResponse<Void> response = ApiResponse.success();

        // when
        String json = response.toJson();

        // then
        assertThat(json).isEqualTo("{\"success\":true,\"data\":null}");
    }
}
