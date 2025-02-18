package com.stempo.service;

import com.stempo.dto.response.PersonalRhythmSettingsResponseDto;
import com.stempo.dto.response.RecordReportResponseDto;
import com.stempo.dto.response.RhythmReportResponseDto;
import java.time.LocalDate;
import java.util.List;

public interface RecordReportService {

    List<RecordReportResponseDto> getRecordReport(List<String> deviceTags, LocalDate startDate, LocalDate endDate);

    List<RhythmReportResponseDto> getRhythmReport(List<String> deviceTags, LocalDate startDate, LocalDate endDate);

    List<PersonalRhythmSettingsResponseDto> getPersonalRhythmSettings(List<String> deviceTags);
}
