package com.stempo.api.domain.persistence.repository;

import com.stempo.api.domain.persistence.entity.UserAchievementEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface UserAchievementJpaRepository extends JpaRepository<UserAchievementEntity, Long> {

    @Query("SELECT ua " +
            "FROM UserAchievementEntity ua " +
            "WHERE ua.deviceTag = :deviceTag AND ua.deleted = false " +
            "ORDER BY ua.createdAt DESC")
    List<UserAchievementEntity> findByDeviceTag(String deviceTag);

    Optional<UserAchievementEntity> findByDeviceTagAndAchievementId(String deviceTag, Long achievementId);

    List<UserAchievementEntity> findByAchievementId(Long achievementId);
}
