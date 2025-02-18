package com.stempo.controller;

import com.stempo.dto.ApiResponse;
import com.stempo.dto.response.PersonalRhythmSettingsResponseDto;
import com.stempo.dto.response.PersonalTrainingSettingsResponseDto;
import com.stempo.dto.response.RecordReportResponseDto;
import com.stempo.dto.response.RhythmReportResponseDto;
import com.stempo.dto.response.UserDataResponseDto;
import com.stempo.service.RecordReportService;
import com.stempo.service.UserDataAggregationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Tag(name = "Admin Report", description = "사용자 활동 데이터 집계 및 분석")
public class AdminReportController {

    private final UserDataAggregationService userDataAggregationService;
    private final RecordReportService recordReportService;

    @Operation(summary = "[A] 사용자 전체 이용 데이터 조회", description = "ROLE_ADMIN 이상의 권한이 필요함<br>"
        + "사용자의 보행 훈련 및 과제 데이터를 조회함")
    @GetMapping("/api/v1/admin/report/user-data")
    public ApiResponse<List<UserDataResponseDto>> getUserData(
        @RequestParam(name = "deviceTags") List<String> deviceTags
    ) {
        List<UserDataResponseDto> userData = userDataAggregationService.getUserData(deviceTags);
        return ApiResponse.success(userData);
    }

    @Operation(summary = "[A] 사용자 보행 훈련 기록 조회", description = "ROLE_ADMIN 이상의 권한이 필요함")
    @GetMapping("/api/v1/admin/report/user-data/training")
    public ApiResponse<List<RecordReportResponseDto>> getRecordReport(
        @RequestParam(name = "deviceTags") List<String> deviceTags,
        @RequestParam(name = "startDate", required = false) LocalDate startDate,
        @RequestParam(name = "endDate", required = false) LocalDate endDate
    ) {
        List<RecordReportResponseDto> recordReport =
            recordReportService.getRecordReport(deviceTags, startDate, endDate);
        return ApiResponse.success(recordReport);
    }

    @Operation(summary = "[A] 사용자 보행 분석 지표 설정값 조회", description = "ROLE_ADMIN 이상의 권한이 필요함<br>"
        + "사용자의 첫 번째 보행 훈련 데이터와 과제 데이터를 조회함")
    @GetMapping("/api/v1/admin/report/user-data/training-setting")
    public ApiResponse<List<PersonalTrainingSettingsResponseDto>> getPersonalTrainingSettings(
        @RequestParam(name = "deviceTags") List<String> deviceTags
    ) {
        List<PersonalTrainingSettingsResponseDto> personalTrainingSettings =
            userDataAggregationService.getPersonalTrainingSettings(deviceTags);
        return ApiResponse.success(personalTrainingSettings);
    }

    @Operation(summary = "[A] 사용자 보행 훈련에 사용된 리듬 데이터 조회", description = "ROLE_ADMIN 이상의 권한이 필요함")
    @GetMapping("/api/v1/admin/report/user-data/rhythm")
    public ApiResponse<List<RhythmReportResponseDto>> getRhythmReport(
        @RequestParam(name = "deviceTags") List<String> deviceTags,
        @RequestParam(name = "startDate", required = false) LocalDate startDate,
        @RequestParam(name = "endDate", required = false) LocalDate endDate
    ) {
        List<RhythmReportResponseDto> recordReport =
            recordReportService.getRhythmReport(deviceTags, startDate, endDate);
        return ApiResponse.success(recordReport);
    }

    @Operation(summary = "[A] 사용자 맞춤형 리듬 설정값 조회", description = "ROLE_ADMIN 이상의 권한이 필요함<br>"
        + "사용자가 처음 애플리케이션을 실행할 때 온보딩 과정에서 추천받은 리듬 설정값과 마지막으로 실행한 보행 훈련에서 설정한 리듬 설정값을 조회함")
    @GetMapping("/api/v1/admin/report/user-data/rhythm-setting")
    public ApiResponse<List<PersonalRhythmSettingsResponseDto>> getPersonalRhythmSettings(
        @RequestParam(name = "deviceTags") List<String> deviceTags
    ) {
        List<PersonalRhythmSettingsResponseDto> personalRhythmSettings =
            recordReportService.getPersonalRhythmSettings(deviceTags);
        return ApiResponse.success(personalRhythmSettings);
    }
}
