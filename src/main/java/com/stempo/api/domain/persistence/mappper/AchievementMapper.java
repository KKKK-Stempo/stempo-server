package com.stempo.api.domain.persistence.mappper;

import com.stempo.api.domain.domain.model.Achievement;
import com.stempo.api.domain.persistence.entity.AchievementEntity;
import org.springframework.stereotype.Component;

@Component
public class AchievementMapper {

    public AchievementEntity toEntity(Achievement achievement) {
        return AchievementEntity.builder()
                .id(achievement.getId())
                .name(achievement.getName())
                .description(achievement.getDescription())
                .imageUrl(achievement.getImageUrl())
                .deleted(achievement.isDeleted())
                .build();
    }

    public Achievement toDomain(AchievementEntity entity) {
        return Achievement.builder()
                .id(entity.getId())
                .name(entity.getName())
                .description(entity.getDescription())
                .imageUrl(entity.getImageUrl())
                .deleted(entity.isDeleted())
                .build();
    }
}
