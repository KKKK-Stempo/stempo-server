package com.stempo.api.domain.domain.repository;

import com.stempo.api.domain.domain.model.UserAchievement;
import com.stempo.api.domain.persistence.entity.UserAchievementEntity;

import java.util.List;
import java.util.Optional;

public interface UserAchievementRepository {

    UserAchievement save(UserAchievement userAchievement);

    void saveAll(List<UserAchievement> achievements);

    List<UserAchievementEntity> findByDeviceTag(String deviceTag);

    Optional<UserAchievementEntity> findByDeviceTagAndAchievementId(String deviceTag, Long achievementId);

    List<UserAchievement> findByAchievementId(Long achievementId);
}