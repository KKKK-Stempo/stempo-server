package com.stempo.mapper;

import com.stempo.entity.HomeworkEntity;
import com.stempo.model.Homework;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class HomeworkMapper {

    public HomeworkEntity toEntity(Homework homework) {
        return HomeworkEntity.builder()
                .id(homework.getId())
                .deviceTag(homework.getDeviceTag())
                .description(homework.getDescription())
                .completed(homework.getCompleted() != null ? homework.getCompleted() : false)
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
