package com.stempo.api.domain.application.service;

import com.stempo.api.domain.domain.model.Record;
import com.stempo.api.domain.domain.repository.RecordRepository;
import com.stempo.api.domain.presentation.dto.request.RecordRequestDto;
import com.stempo.api.domain.presentation.dto.response.RecordResponseDto;
import com.stempo.api.domain.presentation.dto.response.RecordStatisticsResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RecordServiceImpl implements RecordService {

    private final UserService userService;
    private final RecordRepository recordRepository;

    @Override
    @Transactional
    public String record(RecordRequestDto requestDto) {
        String deviceTag = userService.getCurrentDeviceTag();
        Record record = RecordRequestDto.toDomain(requestDto, deviceTag);
        return recordRepository.save(record).getDeviceTag();
    }

    @Override
    @Transactional(readOnly = true)
    public List<RecordResponseDto> getRecordsByDateRange(LocalDate startDate, LocalDate endDate) {
        String deviceTag = userService.getCurrentDeviceTag();
        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atStartOfDay().plusDays(1);

        // startDateTime 이전의 가장 최신 데이터 가져오기
        Record latestBeforeStartDate = recordRepository.findLatestBeforeStartDate(deviceTag, startDateTime);

        // startDateTime과 endDateTime 사이의 데이터 가져오기
        List<Record> records = recordRepository.findByDateBetween(deviceTag, startDateTime, endDateTime);

        // 결과 합치기
        List<RecordResponseDto> combinedRecords = new ArrayList<>();
        if (latestBeforeStartDate != null) {
            combinedRecords.add(RecordResponseDto.toDto(latestBeforeStartDate));
        }
        combinedRecords.addAll(records.stream()
                .map(RecordResponseDto::toDto)
                .toList());

        // 데이터가 없을 경우 오늘 날짜로 값을 0으로 설정하여 반환
        if (combinedRecords.isEmpty()) {
            combinedRecords.add(RecordResponseDto.createDefault());
        }
        return combinedRecords;
    }

    @Override
    @Transactional(readOnly = true)
    public RecordStatisticsResponseDto getRecordStatistics() {
        String deviceTag = userService.getCurrentDeviceTag();

        LocalDateTime todayStartDateTime = LocalDate.now().atStartOfDay();
        LocalDateTime todayEndDateTime = todayStartDateTime.plusDays(1);
        LocalDateTime weekStartDateTime = LocalDate.now()
                .with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
                .atStartOfDay();

        // 오늘의 훈련 횟수 계산
        int todayWalkTrainingCount = recordRepository.countByDeviceTagAndCreatedAtBetween(
                deviceTag, todayStartDateTime, todayEndDateTime);

        // 이번 주 훈련 횟수 계산 (월요일부터 오늘까지)
        int weeklyWalkTrainingCount = recordRepository.countByDeviceTagAndCreatedAtBetween(
                deviceTag, weekStartDateTime, todayEndDateTime);

        // 연속된 훈련 일수 계산
        int consecutiveWalkTrainingDays = calculateConsecutiveTrainingDays(deviceTag);

        return RecordStatisticsResponseDto.of(todayWalkTrainingCount, weeklyWalkTrainingCount, consecutiveWalkTrainingDays);
    }

    private int calculateConsecutiveTrainingDays(String deviceTag) {
        List<LocalDateTime> createdDates = recordRepository.findCreatedAtByDeviceTagOrderByCreatedAtDesc(deviceTag);
        int consecutiveDays = 0;
        LocalDate previousDate = null;

        for (LocalDateTime createdAt : createdDates) {
            LocalDate recordDate = createdAt.toLocalDate();

            // 첫 기록이거나, 이전 기록이 하루 전날이면 연속으로 카운트
            if (previousDate == null || previousDate.minusDays(1).isEqual(recordDate)) {
                consecutiveDays++;
                previousDate = recordDate;
            } else {
                // 연속된 날짜가 아니면 중단
                break;
            }
        }
        return consecutiveDays;
    }

}
