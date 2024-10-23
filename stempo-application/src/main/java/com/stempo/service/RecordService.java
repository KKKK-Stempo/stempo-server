package com.stempo.service;

import com.stempo.dto.request.RecordRequestDto;
import com.stempo.dto.response.RecordResponseDto;
import com.stempo.dto.response.RecordStatisticsResponseDto;
import java.time.LocalDate;
import java.util.List;

public interface RecordService {

    String record(RecordRequestDto requestDto);

    List<RecordResponseDto> getRecordsByDateRange(LocalDate startDate, LocalDate endDate);

    RecordStatisticsResponseDto getRecordStatistics();
}
