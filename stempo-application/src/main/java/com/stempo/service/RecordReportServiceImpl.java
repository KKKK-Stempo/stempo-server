package com.stempo.service;

import com.stempo.dto.DecryptedRecord;
import com.stempo.dto.response.PersonalRhythmSettingsResponseDto;
import com.stempo.dto.response.RecordDataResponseDto;
import com.stempo.dto.response.RecordReportResponseDto;
import com.stempo.dto.response.RhythmDataResponseDto;
import com.stempo.dto.response.RhythmReportResponseDto;
import com.stempo.exception.BaseException;
import com.stempo.exception.ErrorCode;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RecordReportServiceImpl implements RecordReportService {

    private final RecordService recordService;

    @Override
    @Transactional(readOnly = true)
    public List<RecordReportResponseDto> getRecordReport(
        List<String> deviceTags, LocalDate startDate, LocalDate endDate) {
        Map<String, List<DecryptedRecord>> recordsByDevice =
            groupRecordsByDevice(deviceTags, startDate, endDate);

        // 각 deviceTag 그룹별로 RecordReportResponseDto 빌드
        return recordsByDevice.entrySet().stream()
            .map(entry -> {
                List<RecordDataResponseDto> recordDatas = entry.getValue().stream()
                    .map(RecordDataResponseDto::from)
                    .sorted(Comparator.comparing(RecordDataResponseDto::getCreatedAt))
                    .toList();

                return RecordReportResponseDto.of(entry.getKey(), recordDatas);
            })
            .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<RhythmReportResponseDto> getRhythmReport(
        List<String> deviceTags, LocalDate startDate, LocalDate endDate) {
        Map<String, List<DecryptedRecord>> recordsByDevice =
            groupRecordsByDevice(deviceTags, startDate, endDate);

        // 각 deviceTag 그룹별로 RhythmReportResponseDto 빌드
        return recordsByDevice.entrySet().stream()
            .map(entry -> {
                List<RhythmDataResponseDto> rhythmDatas = entry.getValue().stream()
                    .map(RhythmDataResponseDto::from)
                    .sorted(Comparator.comparing(RhythmDataResponseDto::getCreatedAt))
                    .toList();

                return RhythmReportResponseDto.of(entry.getKey(), rhythmDatas);
            })
            .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<PersonalRhythmSettingsResponseDto> getPersonalRhythmSettings(List<String> deviceTags) {
        // deviceTags에 대한 복호화된 레코드를 조회
        List<DecryptedRecord> records = recordService.getByDeviceTags(deviceTags);

        // deviceTag가 null이 아닌 레코드들만 deviceTag로 그룹화
        Map<String, List<DecryptedRecord>> recordsByDevice = records.stream()
            .filter(decryptedRecord -> decryptedRecord.getDeviceTag() != null)
            .collect(Collectors.groupingBy(DecryptedRecord::getDeviceTag));

        // 각 그룹에 대해 createdAt 기준 정렬 후 첫번째, 마지막 요소 추출하여 DTO 빌드
        return recordsByDevice.entrySet().stream()
            .map(entry -> {
                List<DecryptedRecord> deviceRecords = entry.getValue();

                if (deviceRecords.isEmpty()) {
                    return PersonalRhythmSettingsResponseDto.create(entry.getKey());
                }

                // createdAt 기준 오름차순 정렬
                List<DecryptedRecord> sortedRecords = deviceRecords.stream()
                    .sorted(Comparator.comparing(DecryptedRecord::getCreatedAt))
                    .toList();

                // 첫 번째 요소와 마지막 요소 추출
                DecryptedRecord firstRecord = sortedRecords.getFirst();
                DecryptedRecord lastRecord = sortedRecords.getLast();

                return PersonalRhythmSettingsResponseDto.builder()
                    .deviceTag(entry.getKey())
                    .onboardingBit(firstRecord.getBit())
                    .onboardingBpm(firstRecord.getBpm())
                    .lastRecordBit(lastRecord.getBit())
                    .lastRecordBpm(lastRecord.getBpm())
                    .build();
            })
            .toList();
    }

    private Map<String, List<DecryptedRecord>> groupRecordsByDevice(List<String> deviceTags, LocalDate startDate,
        LocalDate endDate) {
        validateDateRange(startDate, endDate);

        // 지정된 deviceTags와 날짜 범위에 해당하는 복호화된 기록들을 조회
        List<DecryptedRecord> records = recordService.getByDeviceTagsAndDateRange(deviceTags, startDate, endDate);

        // deviceTag가 null인 경우를 방지하고 그룹화
        return records.stream()
            .filter(decryptedRecord -> decryptedRecord.getDeviceTag() != null)
            .collect(Collectors.groupingBy(DecryptedRecord::getDeviceTag));
    }

    private void validateDateRange(LocalDate startDate, LocalDate endDate) {
        // 시작일과 종료일이 모두 null이 아닌 경우 유효성 검사
        if (startDate != null && endDate != null && startDate.isAfter(endDate)) {
            throw new BaseException(ErrorCode.INVALID_DATE_RANGE);
        }
    }
}
