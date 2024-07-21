package com.stempo.api.domain.application.event;

import com.stempo.api.domain.domain.model.UserAchievement;
import com.stempo.api.domain.domain.repository.UserAchievementRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
public class UserAchievementEventProcessor implements AchievementEventProcessor {

    private final UserAchievementRepository userAchievementRepository;

    @Override
    @Transactional
    public void processAchievementDeleted(Long achievementId) {
        List<UserAchievement> achievements = userAchievementRepository.findByAchievementId(achievementId);
        achievements.forEach(UserAchievement::delete);
        userAchievementRepository.saveAll(achievements);
    }

    @Override
    public void processAchievementUpdated(Long achievementId) {
        // do nothing
    }
}
