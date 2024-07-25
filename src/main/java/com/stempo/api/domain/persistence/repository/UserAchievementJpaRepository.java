package com.stempo.api.domain.persistence.repository;

import com.stempo.api.domain.persistence.entity.UserAchievementEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserAchievementJpaRepository extends JpaRepository<UserAchievementEntity, Long> {

    Optional<UserAchievementEntity> findByDeviceTagAndAchievementId(String deviceTag, Long achievementId);

    List<UserAchievementEntity> findByAchievementId(Long achievementId);
}
