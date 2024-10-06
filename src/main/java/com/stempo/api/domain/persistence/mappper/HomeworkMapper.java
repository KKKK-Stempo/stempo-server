package com.stempo.api.domain.persistence.mappper;

import com.stempo.api.domain.domain.model.Homework;
import com.stempo.api.domain.persistence.entity.HomeworkEntity;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class HomeworkMapper {

    public HomeworkEntity toEntity(Homework homework) {
        return HomeworkEntity.builder()
                .id(homework.getId())
                .deviceTag(homework.getDeviceTag())
                .description(homework.getDescription())
                .completed(homework.isCompleted())
                .build();
    }

    public List<Homework> toDomain(List<HomeworkEntity> entities) {
        return entities.stream()
                .map(this::toDomain)
                .toList();
    }

    public Homework toDomain(HomeworkEntity entity) {
        return Homework.builder()
                .id(entity.getId())
                .deviceTag(entity.getDeviceTag())
                .description(entity.getDescription())
                .completed(entity.isCompleted())
                .build();
    }
}
