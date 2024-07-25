package com.stempo.api.domain.application.service;

import com.stempo.api.domain.presentation.dto.request.AchievementRequestDto;
import com.stempo.api.domain.presentation.dto.request.AchievementUpdateRequestDto;
import com.stempo.api.domain.presentation.dto.response.AchievementResponseDto;
import com.stempo.api.global.common.dto.PagedResponseDto;
import org.springframework.data.domain.Pageable;

public interface AchievementService {

    Long registerAchievement(AchievementRequestDto requestDto);

    PagedResponseDto<AchievementResponseDto> getAchievements(Pageable pageable);

    Long updateAchievement(Long achievementId, AchievementUpdateRequestDto requestDto);

    Long deleteAchievement(Long achievementId);
}
