package com.stempo.api.domain.presentation.dto.response;

import com.stempo.api.domain.domain.model.Achievement;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class UserAchievementResponseDto {

    private final String name;
    private final String description;
    private final String imageUrl;
    private final LocalDateTime createdAt;

    public static UserAchievementResponseDto toDto(Achievement achievement, LocalDateTime createdAt) {
        return UserAchievementResponseDto.builder()
                .name(achievement.getName())
                .description(achievement.getDescription())
                .imageUrl(achievement.getImageUrl())
                .createdAt(createdAt)
                .build();
    }
}
