package com.stempo.api.domain.presentation.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class RecordStatisticsResponseDto {

    private int todayWalkTrainingCount;
    private int weeklyWalkTrainingCount;
    private int consecutiveWalkTrainingDays;

    public static RecordStatisticsResponseDto of(int todayWalkTrainingCount, int weeklyWalkTrainingCount, int consecutiveWalkTrainingDays) {
        return RecordStatisticsResponseDto.builder()
                .todayWalkTrainingCount(todayWalkTrainingCount)
                .weeklyWalkTrainingCount(weeklyWalkTrainingCount)
                .consecutiveWalkTrainingDays(consecutiveWalkTrainingDays)
                .build();
    }
}
