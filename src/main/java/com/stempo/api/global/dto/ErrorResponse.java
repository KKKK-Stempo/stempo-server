package com.stempo.api.global.dto;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ErrorResponse<T> {

    @Builder.Default
    @Schema(description = "응답 성공 여부", example = "false")
    private Boolean success = false;

    @Schema(description = "응답 데이터")
    private T data;

    @Schema(description = "에러 메시지(예외명)", example = "NullPointerException")
    private String errorMessage;

    public static <T> ErrorResponse<T> failure(Exception e) {
        String exceptionName = e.getClass().getSimpleName();
        return ErrorResponse.<T> builder()
                .errorMessage(exceptionName)
                .build();
    }

    public String toJson() {
        Gson gson = new GsonBuilder().serializeNulls().create();
        return gson.toJson(this);
    }
}
