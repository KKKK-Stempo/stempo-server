package com.stempo.mapper;

import com.stempo.dto.request.HomeworkUpdateRequestDto;
import com.stempo.dto.response.HomeworkResponseDto;
import com.stempo.model.Homework;
import org.springframework.stereotype.Component;

@Component
public class HomeworkDtoMapper {

    public Homework toDomain(HomeworkUpdateRequestDto requestDto) {
        return Homework.builder()
                .description(requestDto.getDescription())
                .completed(requestDto.getCompleted())
                .build();
    }

    public HomeworkResponseDto toDto(Homework homework) {
        return HomeworkResponseDto.builder()
                .id(homework.getId())
                .description(homework.getDescription())
                .completed(homework.getCompleted())
                .build();
    }
}
