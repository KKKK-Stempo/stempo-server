package com.stempo.api.domain.application.service;

import com.stempo.api.domain.presentation.dto.response.UserAchievementResponseDto;

import java.util.List;

public interface UserAchievementService {

    Long registerUserAchievement(Long achievementId);

    List<UserAchievementResponseDto> getUserAchievements();
}
