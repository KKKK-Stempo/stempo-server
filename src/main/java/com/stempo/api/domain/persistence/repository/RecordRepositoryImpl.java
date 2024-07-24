package com.stempo.api.domain.persistence.repository;

import com.stempo.api.domain.domain.model.Record;
import com.stempo.api.domain.domain.repository.RecordRepository;
import com.stempo.api.domain.persistence.entity.RecordEntity;
import com.stempo.api.domain.persistence.mappper.RecordMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class RecordRepositoryImpl implements RecordRepository {

    private final RecordJpaRepository repository;
    private final RecordMapper mapper;

    @Override
    public Record save(Record record) {
        RecordEntity jpaEntity = mapper.toEntity(record);
        RecordEntity savedEntity = repository.save(jpaEntity);
        return mapper.toDomain(savedEntity);
    }

    @Override
    public List<Record> findByDateBetween(LocalDateTime startDateTime, LocalDateTime endDateTime) {
        List<RecordEntity> jpaEntities = repository.findByDateBetween(startDateTime, endDateTime);
        return jpaEntities.stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public Record findLatestBeforeStartDate(LocalDateTime startDateTime) {
        RecordEntity jpaEntity = repository.findLatestBeforeStartDate(startDateTime);
        return mapper.toDomain(jpaEntity);
    }
}
