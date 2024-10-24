package com.stempo.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.stempo.dto.response.RecordResponseDto;
import com.stempo.dto.response.RecordStatisticsResponseDto;
import java.time.LocalDate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class RecordDtoMapperTest {

    private RecordDtoMapper recordDtoMapper;

    @BeforeEach
    void setUp() {
        recordDtoMapper = new RecordDtoMapper();
    }

    @Test
    void accuracy와_duration과_steps와_date로_RecordResponseDto로_매핑된다() {
        // given
        Double accuracy = 95.5;
        Integer duration = 1200;
        Integer steps = 1500;
        LocalDate date = LocalDate.of(2023, 10, 24);

        // when
        RecordResponseDto responseDto = recordDtoMapper.toDto(accuracy, duration, steps, date);

        // then
        assertThat(responseDto.getAccuracy()).isEqualTo(accuracy);
        assertThat(responseDto.getDuration()).isEqualTo(duration);
        assertThat(responseDto.getSteps()).isEqualTo(steps);
        assertThat(responseDto.getDate()).isEqualTo(date);
    }

    @Test
    void 오늘_보행_훈련_횟수와_주간_보행_훈련_횟수와_연속_보행_훈련_일수로_RecordStatisticsResponseDto로_매핑된다() {
        // given
        int todayWalkTrainingCount = 3;
        int weeklyWalkTrainingCount = 10;
        int consecutiveWalkTrainingDays = 5;

        // when
        RecordStatisticsResponseDto responseDto = recordDtoMapper.toDto(todayWalkTrainingCount, weeklyWalkTrainingCount,
                consecutiveWalkTrainingDays);

        // then
        assertThat(responseDto.getTodayWalkTrainingCount()).isEqualTo(todayWalkTrainingCount);
        assertThat(responseDto.getWeeklyWalkTrainingCount()).isEqualTo(weeklyWalkTrainingCount);
        assertThat(responseDto.getConsecutiveWalkTrainingDays()).isEqualTo(consecutiveWalkTrainingDays);
    }
}
