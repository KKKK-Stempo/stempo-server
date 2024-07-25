package com.stempo.api.domain.persistence.repository;

import com.stempo.api.domain.persistence.entity.AchievementEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface AchievementJpaRepository extends JpaRepository<AchievementEntity, Long> {

    @Query("SELECT a " +
            "FROM AchievementEntity a " +
            "WHERE a.deleted = false")
    Page<AchievementEntity> findAllActiveAchievements(Pageable pageable);

    @Query("SELECT a " +
            "FROM AchievementEntity a " +
            "WHERE a.id = :achievementId AND a.deleted = false")
    Optional<AchievementEntity> findByIdAndNotDeleted(@Param("achievementId") Long achievementId);
}
