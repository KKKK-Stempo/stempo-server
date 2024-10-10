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
}
