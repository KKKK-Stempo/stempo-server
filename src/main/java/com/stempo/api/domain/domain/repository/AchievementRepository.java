package com.stempo.api.domain.domain.repository;

import com.stempo.api.domain.domain.model.Achievement;

import java.util.List;

public interface AchievementRepository {

    Achievement findByIdOrThrow(Long achievementId);

    List<Achievement> findAll();

    Achievement save(Achievement achievement);
}
