package com.stempo.repository;

import com.stempo.entity.RecordEntity;
import com.stempo.mapper.RecordMapper;
import com.stempo.model.Record;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class RecordRepositoryImpl implements RecordRepository {

    private final RecordJpaRepository repository;
    private final RecordMapper mapper;

    @Override
    public Record save(Record record) {
        RecordEntity entity = mapper.toEntity(record);
        RecordEntity savedEntity = repository.save(entity);
        return mapper.toDomain(savedEntity);
    }

    @Override
    public void deleteAll(List<Record> records) {
        List<RecordEntity> entities = records.stream()
                .map(mapper::toEntity)
                .toList();
        repository.deleteAll(entities);
    }

    @Override
    public List<Record> findByDateBetween(String deviceTag, LocalDateTime startDateTime, LocalDateTime endDateTime) {
        List<RecordEntity> entities = repository.findByDateBetween(deviceTag, startDateTime, endDateTime);
        return entities.stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public Optional<Record> findLatestBeforeStartDate(String deviceTag, LocalDateTime startDateTime) {
        return repository.findLatestBeforeStartDate(deviceTag, startDateTime)
                .map(mapper::toDomain);
    }

    @Override
    public List<Record> findByDeviceTag(String deviceTag) {
        List<RecordEntity> entities = repository.findByDeviceTag(deviceTag);
        return entities.stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public List<LocalDateTime> findCreatedAtByDeviceTagOrderByCreatedAtDesc(String deviceTag) {
        return repository.findCreatedAtByDeviceTagOrderByCreatedAtDesc(deviceTag);
    }

    @Override
    public int countByDeviceTagAndCreatedAtBetween(String deviceTag, LocalDateTime startDateTime,
            LocalDateTime endDateTime) {
        return repository.countByDeviceTagAndCreatedAtBetween(deviceTag, startDateTime, endDateTime);
    }
}
