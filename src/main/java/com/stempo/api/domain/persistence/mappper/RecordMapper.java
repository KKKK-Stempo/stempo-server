package com.stempo.api.domain.persistence.mappper;

import com.stempo.api.domain.domain.model.Record;
import com.stempo.api.domain.persistence.entity.RecordEntity;
import org.springframework.stereotype.Component;

@Component
public class RecordMapper {

    public RecordEntity toEntity(Record record) {
        return RecordEntity.builder()
                .id(record.getId())
                .deviceTag(record.getDeviceTag())
                .accuracy(record.getAccuracy())
                .duration(record.getDuration())
                .steps(record.getSteps())
                .build();
    }

    public Record toDomain(RecordEntity entity) {
        return Record.builder()
                .id(entity.getId())
                .deviceTag(entity.getDeviceTag())
                .accuracy(entity.getAccuracy())
                .duration(entity.getDuration())
                .steps(entity.getSteps())
                .createdAt(entity.getCreatedAt())
                .build();
    }
}
