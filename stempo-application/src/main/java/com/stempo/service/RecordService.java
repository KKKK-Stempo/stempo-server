package com.stempo.service;

import com.stempo.dto.DecryptedRecord;
import com.stempo.dto.request.RecordRequestDto;
import com.stempo.dto.response.RecordResponseDto;
import com.stempo.dto.response.RecordStatisticsResponseDto;
import java.time.LocalDate;
import java.util.List;

public interface RecordService {

    String recordTrainingData(String deviceTag, RecordRequestDto requestDto);

    RecordResponseDto getRecordsByDateRange(String deviceTag, LocalDate startDate, LocalDate endDate);

    RecordStatisticsResponseDto getRecordStatistics(String deviceTag);

    List<DecryptedRecord> getByDeviceTags(List<String> deviceTags);

    List<DecryptedRecord> getByDeviceTagsAndDateRange(List<String> deviceTags, LocalDate startDate, LocalDate endDate);
}
