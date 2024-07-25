package com.stempo.api.domain.application.service;

import com.stempo.api.domain.application.event.AchievementDeletedEvent;
import com.stempo.api.domain.application.event.AchievementUpdatedEvent;
import com.stempo.api.domain.domain.model.Achievement;
import com.stempo.api.domain.domain.repository.AchievementRepository;
import com.stempo.api.domain.presentation.dto.request.AchievementRequestDto;
import com.stempo.api.domain.presentation.dto.request.AchievementUpdateRequestDto;
import com.stempo.api.domain.presentation.dto.response.AchievementResponseDto;
import com.stempo.api.global.common.dto.PagedResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AchievementServiceImpl implements AchievementService {

    private final AchievementRepository repository;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    public Long registerAchievement(AchievementRequestDto requestDto) {
        Achievement achievement = AchievementRequestDto.toDomain(requestDto);
        return repository.save(achievement).getId();
    }

    @Override
    public PagedResponseDto<AchievementResponseDto> getAchievements(Pageable pageable) {
        Page<Achievement> achievements = repository.findAll(pageable);
        return new PagedResponseDto<>(achievements.map(AchievementResponseDto::toDto));
    }

    @Override
    public Long updateAchievement(Long achievementId, AchievementUpdateRequestDto requestDto) {
        Achievement achievement = repository.findByIdOrThrow(achievementId);
        achievement.update(requestDto);
        repository.save(achievement);
        eventPublisher.publishEvent(new AchievementUpdatedEvent(this, achievement.getId()));
        return achievement.getId();
    }

    @Override
    public Long deleteAchievement(Long achievementId) {
        Achievement achievement = repository.findByIdOrThrow(achievementId);
        achievement.delete();
        repository.save(achievement);
        eventPublisher.publishEvent(new AchievementDeletedEvent(this, achievement.getId()));
        return achievement.getId();
    }
}
