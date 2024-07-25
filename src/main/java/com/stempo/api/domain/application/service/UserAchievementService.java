package com.stempo.api.domain.application.service;

import com.stempo.api.domain.presentation.dto.response.UserAchievementResponseDto;
import com.stempo.api.global.common.dto.PagedResponseDto;
import org.springframework.data.domain.Pageable;

public interface UserAchievementService {

    Long registerUserAchievement(Long achievementId);

    PagedResponseDto<UserAchievementResponseDto> getUserAchievements(Pageable pageable);
}
