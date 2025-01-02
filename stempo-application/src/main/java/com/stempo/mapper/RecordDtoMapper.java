package com.stempo.mapper;

import com.stempo.dto.response.RecordItemDto;
import com.stempo.dto.response.RecordResponseDto;
import com.stempo.dto.response.RecordStatisticsResponseDto;
import java.time.LocalDate;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class RecordDtoMapper {

    public RecordResponseDto toDto(int accuracyAverage, List<RecordItemDto> records) {
        return RecordResponseDto.builder()
            .accuracyAverage(accuracyAverage)
            .records(records)
            .build();
    }

    public RecordItemDto toDto(Double accuracy, Integer duration, Integer steps, LocalDate date) {
        return RecordItemDto.builder()
            .accuracy(accuracy)
            .duration(duration)
            .steps(steps)
            .date(date)
            .build();
    }

    public RecordStatisticsResponseDto toDto(int todayWalkTrainingCount, int weeklyWalkTrainingCount,
        int consecutiveWalkTrainingDays) {
        return RecordStatisticsResponseDto.builder()
            .todayWalkTrainingCount(todayWalkTrainingCount)
            .weeklyWalkTrainingCount(weeklyWalkTrainingCount)
            .consecutiveWalkTrainingDays(consecutiveWalkTrainingDays)
            .build();
    }
}
