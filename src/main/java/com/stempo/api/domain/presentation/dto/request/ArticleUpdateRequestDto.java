package com.stempo.api.domain.presentation.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ArticleUpdateRequestDto {

    @Schema(description = "제목", example = "뇌성마비 의학정보 바로가기", minLength = 1, maxLength = 100, requiredMode = Schema.RequiredMode.REQUIRED)
    private String title;

    @Schema(description = "내용", example = "뇌성마비 환자를 위한 의학정보를 제공하는 사이트입니다.", minLength = 1, maxLength = 10000, requiredMode = Schema.RequiredMode.REQUIRED)
    private String content;

    @Schema(description = "썸네일 URL", example = "https://example.com/thumbnail.jpg", minLength = 1, maxLength = 255)
    private String thumbnailUrl;

    @Schema(description = "기사 URL", example = "https://example.com/article", minLength = 1, maxLength = 255, requiredMode = Schema.RequiredMode.REQUIRED)
    private String articleUrl;
}
