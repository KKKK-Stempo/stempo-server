package com.stempo.api.domain.presentation.dto.response;

import com.stempo.api.domain.domain.model.Homework;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class HomeworkResponseDto {

    private Long id;
    private String description;
    private boolean completed;

    public static HomeworkResponseDto toDto(Homework homework) {
        return HomeworkResponseDto.builder()
                .id(homework.getId())
                .description(homework.getDescription())
                .completed(homework.isCompleted())
                .build();
    }
}
