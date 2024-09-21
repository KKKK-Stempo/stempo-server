package com.stempo.api.domain.persistence.repository;

import com.stempo.api.domain.domain.model.Homework;
import com.stempo.api.domain.domain.repository.HomeworkRepository;
import com.stempo.api.domain.persistence.entity.HomeworkEntity;
import com.stempo.api.domain.persistence.mappper.HomeworkMapper;
import com.stempo.api.global.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.Optional;

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
    public void delete(Homework homework) {
        HomeworkEntity entity = mapper.toEntity(homework);
        repository.delete(entity);
    }
}
