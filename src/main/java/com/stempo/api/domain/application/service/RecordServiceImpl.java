package com.stempo.api.domain.application.service;

import com.stempo.api.domain.domain.model.Record;
import com.stempo.api.domain.domain.repository.RecordRepository;
import com.stempo.api.domain.presentation.dto.request.RecordRequestDto;
import com.stempo.api.domain.presentation.dto.response.RecordResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
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
        List<Record> records = recordRepository.findByDateBetween(startDateTime, endDateTime);
        return records.stream()
                .map(RecordResponseDto::toDto)
                .toList();
    }
}
