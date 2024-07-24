package com.stempo.api.domain.presentation.dto.request;

import com.stempo.api.domain.domain.model.Board;
import com.stempo.api.domain.domain.model.BoardCategory;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BoardRequestDto {

    @NotNull
    @Schema(description = "카테고리", example = "NOTICE", requiredMode = Schema.RequiredMode.REQUIRED)
    private BoardCategory category;

    @NotNull
    @Schema(description = "제목", example = "청각 자극을 통한 뇌성마비 환자 보행 패턴 개선 서비스, Stempo.", minLength = 1, maxLength = 100, requiredMode = Schema.RequiredMode.REQUIRED)
    private String title;

    @NotNull
    @Schema(description = "내용", example = "Stempo는 청각 자극을 통한 뇌성마비 환자 보행 패턴 개선 서비스입니다.", minLength = 1, maxLength = 10000, requiredMode = Schema.RequiredMode.REQUIRED)
    private String content;

    public static Board toDomain(BoardRequestDto requestDto, String deviceTag) {
        return Board.builder()
                .deviceTag(deviceTag)
                .category(requestDto.getCategory())
                .title(requestDto.getTitle())
                .content(requestDto.getContent())
                .build();
    }
}
