package com.stempo.api.domain.domain.repository;

import com.stempo.api.domain.domain.model.Record;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface RecordRepository {

    Record save(Record record);

    void deleteAll(List<Record> records);

    List<Record> findByDateBetween(String deviceTag, LocalDateTime startDateTime, LocalDateTime endDateTime);

    Optional<Record> findLatestBeforeStartDate(String deviceTag, LocalDateTime startDateTime);

    List<Record> findByDeviceTag(String deviceTag);

    List<LocalDateTime> findCreatedAtByDeviceTagOrderByCreatedAtDesc(String deviceTag);

    int countByDeviceTagAndCreatedAtBetween(String deviceTag, LocalDateTime startDateTime, LocalDateTime endDateTime);
}
