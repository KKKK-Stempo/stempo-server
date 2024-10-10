package com.stempo.api.domain.presentation.mapper;

import com.stempo.api.domain.domain.model.Homework;
import com.stempo.api.domain.presentation.dto.response.HomeworkResponseDto;
import org.springframework.stereotype.Component;

@Component
public class HomeworkDtoMapper {

    public HomeworkResponseDto toDto(Homework homework) {
        return HomeworkResponseDto.builder()
                .id(homework.getId())
                .description(homework.getDescription())
                .completed(homework.isCompleted())
                .build();
    }
}
