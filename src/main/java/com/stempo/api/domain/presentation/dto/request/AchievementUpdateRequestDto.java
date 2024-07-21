package com.stempo.api.domain.presentation.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AchievementUpdateRequestDto {

    @Schema(description = "업적 이름", example = "업적 이름", minLength = 1, maxLength = 100)
    private String name;

    @Schema(description = "업적 설명", example = "업적 설명", minLength = 1, maxLength = 255)
    private String description;

    @Schema(description = "이미지 URL", example = "https://example.com/image.jpg", minLength = 1, maxLength = 255)
    private String imageUrl;
}
