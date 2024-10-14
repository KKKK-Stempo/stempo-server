package com.stempo.mapper;

import com.stempo.dto.response.RecordResponseDto;
import com.stempo.dto.response.RecordStatisticsResponseDto;
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

    public RecordStatisticsResponseDto toDto(int todayWalkTrainingCount, int weeklyWalkTrainingCount, int consecutiveWalkTrainingDays) {
        return RecordStatisticsResponseDto.builder()
                .todayWalkTrainingCount(todayWalkTrainingCount)
                .weeklyWalkTrainingCount(weeklyWalkTrainingCount)
                .consecutiveWalkTrainingDays(consecutiveWalkTrainingDays)
                .build();
    }
}
