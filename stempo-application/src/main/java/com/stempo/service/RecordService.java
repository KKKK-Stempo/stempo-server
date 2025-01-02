package com.stempo.service;

import com.stempo.dto.request.RecordRequestDto;
import com.stempo.dto.response.RecordResponseDto;
import com.stempo.dto.response.RecordStatisticsResponseDto;
import java.time.LocalDate;

public interface RecordService {

    String recordTrainingData(RecordRequestDto requestDto);

    RecordResponseDto getRecordsByDateRange(LocalDate startDate, LocalDate endDate);

    RecordStatisticsResponseDto getRecordStatistics();
}
