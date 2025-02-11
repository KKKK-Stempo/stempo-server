package com.stempo.mapper;

import com.stempo.entity.RecordEntity;
import com.stempo.model.Record;
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
            .leftFootAvgSpeed(record.getLeftFootAvgSpeed())
            .rightFootAvgSpeed(record.getRightFootAvgSpeed())
            .bit(record.getBit())
            .bpm(record.getBpm())
            .build();
    }

    public Record toDomain(RecordEntity entity) {
        return Record.builder()
            .id(entity.getId())
            .deviceTag(entity.getDeviceTag())
            .accuracy(entity.getAccuracy())
            .duration(entity.getDuration())
            .steps(entity.getSteps())
            .leftFootAvgSpeed(entity.getLeftFootAvgSpeed())
            .rightFootAvgSpeed(entity.getRightFootAvgSpeed())
            .bit(entity.getBit())
            .bpm(entity.getBpm())
            .createdAt(entity.getCreatedAt())
            .build();
    }
}
