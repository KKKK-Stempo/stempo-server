package com.stempo.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class RecordStatisticsResponseDto {

    @Schema(description = "오늘 보행 훈련 횟수", example = "0")
    private int todayWalkTrainingCount;

    @Schema(description = "주간 보행 훈련 횟수", example = "0")
    private int weeklyWalkTrainingCount;

    @Schema(description = "연속 보행 훈련 일수", example = "0")
    private int consecutiveWalkTrainingDays;
}
