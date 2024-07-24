package com.stempo.api.domain.presentation.dto.response;

import com.stempo.api.domain.domain.model.Record;
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

    public static RecordResponseDto toDto(Record record) {
        return RecordResponseDto.builder()
                .accuracy(record.getAccuracy())
                .duration(record.getDuration())
                .steps(record.getSteps())
                .date(record.getCreatedAt().toLocalDate())
                .build();
    }

    public static RecordResponseDto createDefault() {
        return RecordResponseDto.builder()
                .accuracy(0.0)
                .duration(0)
                .steps(0)
                .date(LocalDate.now())
                .build();
    }
}
