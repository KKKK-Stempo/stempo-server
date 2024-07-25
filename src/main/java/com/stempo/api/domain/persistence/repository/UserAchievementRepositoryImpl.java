package com.stempo.api.domain.persistence.repository;

import com.stempo.api.domain.domain.model.UserAchievement;
import com.stempo.api.domain.domain.repository.UserAchievementRepository;
import com.stempo.api.domain.persistence.entity.UserAchievementEntity;
import com.stempo.api.domain.persistence.mappper.UserAchievementMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class UserAchievementRepositoryImpl implements UserAchievementRepository {

    private final UserAchievementJpaRepository repository;
    private final UserAchievementMapper mapper;

    @Override
    public UserAchievement save(UserAchievement userAchievement) {
        UserAchievementEntity jpaEntity = mapper.toEntity(userAchievement);
        UserAchievementEntity savedEntity = repository.save(jpaEntity);
        return mapper.toDomain(savedEntity);
    }

    @Override
    public void saveAll(List<UserAchievement> achievements) {
        List<UserAchievementEntity> jpaEntities = achievements.stream()
                .map(mapper::toEntity)
                .toList();
        repository.saveAll(jpaEntities);
    }

    @Override
    public List<UserAchievement> findByDeviceTag(String deviceTag) {
        return repository.findByDeviceTag(deviceTag).stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public Optional<UserAchievement> findByDeviceTagAndAchievementId(String deviceTag, Long achievementId) {
        return repository.findByDeviceTagAndAchievementId(deviceTag, achievementId)
                .map(mapper::toDomain);
    }

    @Override
    public List<UserAchievement> findByAchievementId(Long achievementId) {
        List<UserAchievementEntity> jpaEntities = repository.findByAchievementId(achievementId);
        return jpaEntities.stream()
                .map(mapper::toDomain)
                .toList();
    }
}
