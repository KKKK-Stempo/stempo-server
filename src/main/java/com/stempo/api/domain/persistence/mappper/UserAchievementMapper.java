package com.stempo.api.domain.persistence.mappper;

import com.stempo.api.domain.domain.model.UserAchievement;
import com.stempo.api.domain.persistence.entity.UserAchievementEntity;
import org.springframework.stereotype.Component;

@Component
public class UserAchievementMapper {

    public UserAchievementEntity toEntity(UserAchievement userAchievement) {
        return UserAchievementEntity.builder()
                .id(userAchievement.getId())
                .deviceTag(userAchievement.getDeviceTag())
                .achievementId(userAchievement.getAchievementId())
                .build();
    }

    public UserAchievement toDomain(UserAchievementEntity userAchievementEntity) {
        return UserAchievement.builder()
                .id(userAchievementEntity.getId())
                .deviceTag(userAchievementEntity.getDeviceTag())
                .achievementId(userAchievementEntity.getAchievementId())
                .createdAt(userAchievementEntity.getCreatedAt())
                .build();
    }
}
