package com.stempo.api.domain.presentation.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@Builder
public class RecordResponseDto {

    private Double accuracy;
    private Integer duration;
    private Integer steps;
    private LocalDate date;

    public static RecordResponseDto create(Double accuracy, Integer duration, Integer steps, LocalDate date) {
        return RecordResponseDto.builder()
                .accuracy(accuracy)
                .duration(duration)
                .steps(steps)
                .date(date)
                .build();
    }
}
