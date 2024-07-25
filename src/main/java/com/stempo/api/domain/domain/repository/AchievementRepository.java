package com.stempo.api.domain.domain.repository;

import com.stempo.api.domain.domain.model.Achievement;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AchievementRepository {

    Achievement findByIdOrThrow(Long achievementId);

    Page<Achievement> findAll(Pageable pageable);

    Achievement save(Achievement achievement);
}
