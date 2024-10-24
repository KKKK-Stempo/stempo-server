package com.stempo.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class RecordResponseDto {

    @Schema(description = "정확도", example = "0.0")
    private Double accuracy;

    @Schema(description = "재활 운동 시간(초)", example = "0")
    private Integer duration;

    @Schema(description = "걸음 수", example = "0")
    private Integer steps;

    @Schema(description = "날짜", example = "2024-01-01")
    private LocalDate date;
}
