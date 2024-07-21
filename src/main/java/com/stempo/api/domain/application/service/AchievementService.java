package com.stempo.api.domain.application.service;

import com.stempo.api.domain.presentation.dto.request.AchievementRequestDto;
import com.stempo.api.domain.presentation.dto.request.AchievementUpdateRequestDto;
import com.stempo.api.domain.presentation.dto.response.AchievementResponseDto;

import java.util.List;

public interface AchievementService {

    Long registerAchievement(AchievementRequestDto requestDto);

    List<AchievementResponseDto> getAchievements();

    Long updateAchievement(Long achievementId, AchievementUpdateRequestDto requestDto);

    Long deleteAchievement(Long achievementId);
}
