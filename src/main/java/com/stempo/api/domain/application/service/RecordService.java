package com.stempo.api.domain.application.service;

import com.stempo.api.domain.presentation.dto.request.RecordRequestDto;
import com.stempo.api.domain.presentation.dto.response.RecordResponseDto;

import java.time.LocalDate;
import java.util.List;

public interface RecordService {

    String record(RecordRequestDto requestDto);

    List<RecordResponseDto> getRecordsByDateRange(LocalDate startDate, LocalDate endDate);
}
