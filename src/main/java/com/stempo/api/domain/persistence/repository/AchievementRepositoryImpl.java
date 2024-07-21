package com.stempo.api.domain.persistence.repository;

import com.stempo.api.domain.domain.model.Achievement;
import com.stempo.api.domain.domain.repository.AchievementRepository;
import com.stempo.api.domain.persistence.entity.AchievementEntity;
import com.stempo.api.domain.persistence.mappper.AchievementMapper;
import com.stempo.api.global.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class AchievementRepositoryImpl implements AchievementRepository {

    private final AchievementJpaRepository repository;
    private final AchievementMapper mapper;


    @Override
    public Achievement findByIdOrThrow(Long achievementId) {
        AchievementEntity entity = repository.findByIdAndNotDeleted(achievementId)
                .orElseThrow(() -> new NotFoundException("[Achievement] id: " + achievementId + " not found"));
        return mapper.toDomain(entity);
    }

    @Override
    public List<Achievement> findAll() {
        List<AchievementEntity> entities = repository.findAllActiveAchievements();
        return entities.stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public Achievement save(Achievement achievement) {
        AchievementEntity jpaEntity = mapper.toEntity(achievement);
        AchievementEntity savedEntity = repository.save(jpaEntity);
        return mapper.toDomain(savedEntity);
    }
}
