package com.stempo.api.domain.presentation.mapper;

import com.stempo.api.domain.presentation.dto.response.RecordResponseDto;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class RecordDtoMapper {

    public RecordResponseDto toDto(Double accuracy, Integer duration, Integer steps, LocalDate date) {
        return RecordResponseDto.builder()
                .accuracy(accuracy)
                .duration(duration)
                .steps(steps)
                .date(date)
                .build();
    }
}
