package com.stempo.api.domain.application.event;

public interface AchievementEventProcessor {

    void processAchievementDeleted(Long achievementId);

    void processAchievementUpdated(Long achievementId);
}
