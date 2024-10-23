package com.stempo.repository;

import com.stempo.entity.HomeworkEntity;
import com.stempo.exception.NotFoundException;
import com.stempo.mapper.HomeworkMapper;
import com.stempo.model.Homework;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class HomeworkRepositoryImpl implements HomeworkRepository {

    private final HomeworkJpaRepository repository;
    private final HomeworkMapper mapper;

    @Override
    public Homework save(Homework homework) {
        HomeworkEntity entity = mapper.toEntity(homework);
        HomeworkEntity savedEntity = repository.save(entity);
        return mapper.toDomain(savedEntity);
    }

    @Override
    public void delete(Homework homework) {
        HomeworkEntity entity = mapper.toEntity(homework);
        repository.delete(entity);
    }

    @Override
    public void deleteAll(List<Homework> homeworks) {
        List<HomeworkEntity> entities = homeworks.stream()
                .map(mapper::toEntity)
                .toList();
        repository.deleteAll(entities);
    }

    @Override
    public Page<Homework> findByCompleted(Boolean completed, Pageable pageable) {
        return Optional.ofNullable(completed)
                .map(c -> repository.findByCompleted(c, pageable))
                .orElseGet(() -> repository.findAll(pageable))
                .map(mapper::toDomain);
    }


    @Override
    public Homework findByIdOrThrow(Long homeworkId) {
        return repository.findById(homeworkId)
                .map(mapper::toDomain)
                .orElseThrow(() -> new NotFoundException("[Homework] id: " + homeworkId + " not found"));
    }

    @Override
    public List<Homework> findByDeviceTag(String deviceTag) {
        List<HomeworkEntity> entities = repository.findByDeviceTag(deviceTag);
        return entities.stream()
                .map(mapper::toDomain)
                .toList();
    }
}
