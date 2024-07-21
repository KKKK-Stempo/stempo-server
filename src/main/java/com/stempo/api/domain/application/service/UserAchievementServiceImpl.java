package com.stempo.api.domain.application.service;

import com.stempo.api.domain.domain.model.Achievement;
import com.stempo.api.domain.domain.model.UserAchievement;
import com.stempo.api.domain.domain.repository.AchievementRepository;
import com.stempo.api.domain.domain.repository.UserAchievementRepository;
import com.stempo.api.domain.persistence.entity.UserAchievementEntity;
import com.stempo.api.domain.presentation.dto.response.UserAchievementResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserAchievementServiceImpl implements UserAchievementService {

    private final UserService userService;
    private final UserAchievementRepository userAchievementRepository;
    private final AchievementRepository achievementRepository;

    @Override
    public Long registerUserAchievement(Long achievementId) {
        String deviceTag = userService.getCurrentDeviceTag();
        Optional<UserAchievementEntity> existingUserAchievement = userAchievementRepository.findByDeviceTagAndAchievementId(deviceTag, achievementId);

        if (existingUserAchievement.isPresent()) {
            return existingUserAchievement.get().getId();
        }

        UserAchievement userAchievement = UserAchievement.create(achievementId, deviceTag);
        return userAchievementRepository.save(userAchievement).getId();
    }

    @Override
    public List<UserAchievementResponseDto> getUserAchievements() {
        String deviceTag = userService.getCurrentDeviceTag();
        List<UserAchievementEntity> userAchievements = userAchievementRepository.findByDeviceTag(deviceTag);
        return userAchievements.stream()
                .map(this::getUserAchievementResponseDto)
                .toList();
    }

    private UserAchievementResponseDto getUserAchievementResponseDto(UserAchievementEntity ua) {
        Achievement achievement = achievementRepository.findByIdOrThrow(ua.getAchievementId());
        return UserAchievementResponseDto.toDto(achievement);
    }
}
