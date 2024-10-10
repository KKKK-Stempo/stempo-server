package com.stempo.api.domain.application.service;

import com.stempo.api.domain.domain.model.Record;
import com.stempo.api.domain.domain.repository.RecordRepository;
import com.stempo.api.domain.presentation.dto.request.RecordRequestDto;
import com.stempo.api.domain.presentation.dto.response.RecordResponseDto;
import com.stempo.api.domain.presentation.dto.response.RecordStatisticsResponseDto;
import com.stempo.api.domain.presentation.mapper.RecordDtoMapper;
import com.stempo.api.global.util.EncryptionUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RecordServiceImpl implements RecordService {

    private final UserService userService;
    private final RecordRepository recordRepository;
    private final RecordDtoMapper mapper;
    private final EncryptionUtil encryptionUtil;

    @Override
    @Transactional
    public String record(RecordRequestDto requestDto) {
        String deviceTag = userService.getCurrentDeviceTag();
        String encryptedAccuracy = encryptionUtil.encrypt(requestDto.getAccuracy().toString());
        String encryptedDuration = encryptionUtil.encrypt(requestDto.getDuration().toString());
        String encryptedSteps = encryptionUtil.encrypt(requestDto.getSteps().toString());

        Record record = Record.create(deviceTag, encryptedAccuracy, encryptedDuration, encryptedSteps);
        return recordRepository.save(record).getDeviceTag();
    }

    @Override
    @Transactional(readOnly = true)
    public List<RecordResponseDto> getRecordsByDateRange(LocalDate startDate, LocalDate endDate) {
        String deviceTag = userService.getCurrentDeviceTag();
        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atStartOfDay().plusDays(1);

        // startDateTime 이전의 가장 최신 데이터 가져오기
        Optional<Record> latestBeforeStartDate = recordRepository.findLatestBeforeStartDate(deviceTag, startDateTime);

        // startDateTime과 endDateTime 사이의 데이터 가져오기
        List<Record> records = recordRepository.findByDateBetween(deviceTag, startDateTime, endDateTime);

        // 결과 합치기
        List<RecordResponseDto> combinedRecords = new ArrayList<>();
        latestBeforeStartDate.ifPresentOrElse(
                record -> combinedRecords.add(convertToDecryptedDto(record)),
                () -> combinedRecords.add(mapper.toDto(0.0, 0, 0, startDate.minusDays(1)))
        );

        combinedRecords.addAll(records.stream()
                .map(this::convertToDecryptedDto)
                .toList());

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

        return mapper.toDto(todayWalkTrainingCount, weeklyWalkTrainingCount, consecutiveWalkTrainingDays);
    }

    private RecordResponseDto convertToDecryptedDto(Record record) {
        Double decryptedAccuracy = Double.parseDouble(encryptionUtil.decrypt(record.getAccuracy()));
        Integer decryptedDuration = Integer.parseInt(encryptionUtil.decrypt(record.getDuration()));
        Integer decryptedSteps = Integer.parseInt(encryptionUtil.decrypt(record.getSteps()));
        LocalDate date = record.getCreatedAt().toLocalDate();

        return mapper.toDto(decryptedAccuracy, decryptedDuration, decryptedSteps, date);
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
