package com.stempo.api.domain.presentation.dto.response;

import com.stempo.api.domain.domain.model.Achievement;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AchievementResponseDto {

    private Long id;
    private String name;
    private String description;
    private String imageUrl;

    public static AchievementResponseDto toDto(Achievement achievement) {
        return AchievementResponseDto.builder()
                .id(achievement.getId())
                .name(achievement.getName())
                .description(achievement.getDescription())
                .imageUrl(achievement.getImageUrl())
                .build();
    }
}
