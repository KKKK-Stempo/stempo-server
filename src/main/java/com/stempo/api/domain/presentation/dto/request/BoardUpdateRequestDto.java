package com.stempo.api.domain.presentation.dto.request;

import com.stempo.api.domain.domain.model.BoardCategory;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BoardUpdateRequestDto {

    @Schema(description = "카테고리", example = "NOTICE", requiredMode = Schema.RequiredMode.REQUIRED)
    private BoardCategory category;

    @Schema(description = "제목", example = "청각 자극을 통한 뇌성마비 환자 보행 패턴 개선 서비스, Stempo.", minLength = 1, maxLength = 100, requiredMode = Schema.RequiredMode.REQUIRED)
    private String title;

    @Schema(description = "내용", example = "Stempo는 청각 자극을 통한 뇌성마비 환자 보행 패턴 개선 서비스입니다.", minLength = 1, maxLength = 10000, requiredMode = Schema.RequiredMode.REQUIRED)
    private String content;
}
