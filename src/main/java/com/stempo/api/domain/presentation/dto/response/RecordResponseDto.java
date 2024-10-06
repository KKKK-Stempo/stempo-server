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

    public static RecordResponseDto createDefault() {
        return RecordResponseDto.builder()
                .accuracy(0.0)
                .duration(0)
                .steps(0)
                .date(LocalDate.now())
                .build();
    }

    public static RecordResponseDto create(String accuracy, String duration, String steps, LocalDate date) {
        return RecordResponseDto.builder()
                .accuracy(Double.parseDouble(accuracy))
                .duration(Integer.parseInt(duration))
                .steps(Integer.parseInt(steps))
                .date(date)
                .build();
    }
}
