package com.stempo.api.domain.application.service;

import com.stempo.api.domain.domain.model.Record;
import com.stempo.api.domain.domain.repository.RecordRepository;
import com.stempo.api.domain.presentation.dto.request.RecordRequestDto;
import com.stempo.api.domain.presentation.dto.response.RecordResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RecordServiceImpl implements RecordService {

    private final UserService userService;
    private final RecordRepository recordRepository;

    @Override
    public String record(RecordRequestDto requestDto) {
        String deviceTag = userService.getCurrentDeviceTag();
        Record record = RecordRequestDto.toDomain(requestDto, deviceTag);
        return recordRepository.save(record).getDeviceTag();
    }

    @Override
    public List<RecordResponseDto> getRecordsByDateRange(LocalDate startDate, LocalDate endDate) {
        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atStartOfDay().plusDays(1);

        // startDateTime 이전의 가장 최신 데이터 가져오기
        Record latestBeforeStartDate = recordRepository.findLatestBeforeStartDate(startDateTime);

        // startDateTime과 endDateTime 사이의 데이터 가져오기
        List<Record> records = recordRepository.findByDateBetween(startDateTime, endDateTime);

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
}
