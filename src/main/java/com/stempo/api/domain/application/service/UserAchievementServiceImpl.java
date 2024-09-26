package com.stempo.api.domain.application.service;

import com.stempo.api.domain.domain.model.Achievement;
import com.stempo.api.domain.domain.model.UserAchievement;
import com.stempo.api.domain.domain.repository.AchievementRepository;
import com.stempo.api.domain.domain.repository.UserAchievementRepository;
import com.stempo.api.domain.presentation.dto.response.UserAchievementResponseDto;
import com.stempo.api.global.common.dto.PagedResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserAchievementServiceImpl implements UserAchievementService {

    private final UserService userService;
    private final UserAchievementRepository userAchievementRepository;
    private final AchievementRepository achievementRepository;

    @Override
    @Transactional
    public Long registerUserAchievement(Long achievementId) {
        String deviceTag = userService.getCurrentDeviceTag();
        Optional<UserAchievement> existingUserAchievement =
                userAchievementRepository.findByDeviceTagAndAchievementId(deviceTag, achievementId);

        if (existingUserAchievement.isPresent()) {
            return existingUserAchievement.get().getId();
        }

        UserAchievement userAchievement = UserAchievement.create(achievementId, deviceTag);
        return userAchievementRepository.save(userAchievement).getId();
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResponseDto<UserAchievementResponseDto> getUserAchievements(Pageable pageable) {
        String deviceTag = userService.getCurrentDeviceTag();
        Page<Achievement> achievements = achievementRepository.findAll(pageable);
        List<UserAchievementResponseDto> userAchievementDtos = getUserAchievementResponseDtos(achievements, deviceTag);
        return new PagedResponseDto<>(new PageImpl<>(userAchievementDtos, pageable, userAchievementDtos.size()));
    }

    private List<UserAchievementResponseDto> getUserAchievementResponseDtos(Page<Achievement> achievements, String deviceTag) {
        return achievements.stream()
                .map(achievement -> userAchievementRepository.findByDeviceTagAndAchievementId(deviceTag, achievement.getId())
                            .map(userAchievement -> UserAchievementResponseDto.toDto(achievement, userAchievement.getCreatedAt()))
                            .orElse(null)
                )
                .filter(Objects::nonNull)
                .toList();
    }
}
