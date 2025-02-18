package com.stempo.service;

import com.stempo.dto.DecryptedRecord;
import com.stempo.dto.response.RecordDataResponseDto;
import com.stempo.dto.response.RecordReportResponseDto;
import com.stempo.exception.BaseException;
import com.stempo.exception.ErrorCode;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RecordReportServiceImpl implements RecordReportService {

    private final UserService userService;
    private final RecordService recordService;

    @Override
    public List<RecordReportResponseDto> getRecordReport(
        List<String> deviceTags, LocalDate startDate, LocalDate endDate) {
        // 시작일과 종료일이 모두 null이 아닌 경우 유효성 검사
        if (startDate != null && endDate != null && startDate.isAfter(endDate)) {
            throw new BaseException(ErrorCode.INVALID_DATE_RANGE);
        }

        // 지정된 deviceTags와 날짜 범위에 해당하는 복호화된 기록들을 조회
        List<DecryptedRecord> records = recordService.getByDeviceTagsAndDateRange(deviceTags, startDate, endDate);

        // deviceTag가 null인 경우를 방지하고 그룹화
        Map<String, List<DecryptedRecord>> recordsByDevice = records.stream()
            .filter(decryptedRecord -> decryptedRecord.getDeviceTag() != null)
            .collect(Collectors.groupingBy(DecryptedRecord::getDeviceTag));

        // 각 deviceTag 그룹별로 RecordReportResponseDto 빌드
        return recordsByDevice.entrySet().stream()
            .map(entry -> {
                String decryptedDeviceTag = userService.decryptDeviceTag(entry.getKey());

                List<RecordDataResponseDto> recordDatas = entry.getValue().stream()
                    .map(RecordDataResponseDto::from)
                    .toList();

                return RecordReportResponseDto.of(decryptedDeviceTag, recordDatas);
            })
            .toList();
    }
}
