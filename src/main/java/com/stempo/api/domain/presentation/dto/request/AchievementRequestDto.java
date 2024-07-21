package com.stempo.api.domain.presentation.dto.request;

import com.stempo.api.domain.domain.model.Achievement;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AchievementRequestDto {

    @NotNull
    @Schema(description = "업적 이름", example = "업적 이름", minLength = 1, maxLength = 100)
    private String name;

    @NotNull
    @Schema(description = "업적 설명", example = "업적 설명", minLength = 1, maxLength = 255)
    private String description;

    @NotNull
    @Schema(description = "이미지 URL", example = "https://example.com/image.jpg", minLength = 1, maxLength = 255)
    private String imageUrl;

    public static Achievement toDomain(AchievementRequestDto requestDto) {
        return Achievement.builder()
                .name(requestDto.getName())
                .description(requestDto.getDescription())
                .imageUrl(requestDto.getImageUrl())
                .build();
    }
}
