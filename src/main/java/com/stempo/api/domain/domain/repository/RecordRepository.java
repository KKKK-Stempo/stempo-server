package com.stempo.api.domain.domain.repository;

import com.stempo.api.domain.domain.model.Record;

import java.time.LocalDateTime;
import java.util.List;

public interface RecordRepository {

    Record save(Record record);

    List<Record> findByDateBetween(LocalDateTime startDateTime, LocalDateTime endDateTime);
}
