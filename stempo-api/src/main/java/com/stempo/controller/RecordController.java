package com.stempo.controller;

import com.stempo.annotation.SuccessApiResponse;
import com.stempo.dto.ApiResponse;
import com.stempo.dto.request.RecordRequestDto;
import com.stempo.dto.response.RecordResponseDto;
import com.stempo.dto.response.RecordStatisticsResponseDto;
import com.stempo.service.RecordService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Tag(name = "Record", description = "보행 훈련 기록")
public class RecordController {

    private final RecordService recordService;

    @Operation(summary = "[U] 보행 훈련 기록", description = "ROLE_USER 이상의 권한이 필요함")
    @SuccessApiResponse(data = "deviceTag", dataType = String.class, dataDescription = "사용자의 디바이스 식별자")
    @Secured({ "ROLE_USER", "ROLE_ADMIN" })
    @PostMapping("/api/v1/records")
    public ApiResponse<String> record(
            @Valid @RequestBody RecordRequestDto requestDto
    ) {
        String deviceTag = recordService.record(requestDto);
        return ApiResponse.success(deviceTag);
    }

    @Operation(summary = "[U] 내 보행 훈련 기록 조회", description = "ROLE_USER 이상의 권한이 필요함<br>" +
            "startDate와 endDate는 yyyy-MM-dd 형식으로 입력해야 함<br>" +
            "startDate 이전의 가장 최신 데이터와 startDate부터 endDate까지의 데이터를 가져옴<br>" +
            "데이터가 없을 경우 startDate 이전 날짜, 정확도 0으로 설정하여 반환")
    @Secured({ "ROLE_USER", "ROLE_ADMIN" })
    @GetMapping("/api/v1/records")
    public ApiResponse<List<RecordResponseDto>> getRecords(
            @RequestParam(name = "startDate") LocalDate startDate,
            @RequestParam(name = "endDate") LocalDate endDate
    ) {
        List<RecordResponseDto> records = recordService.getRecordsByDateRange(startDate, endDate);
        return ApiResponse.success(records);
    }

    @Operation(summary = "[U] 내 보행 훈련 기록 통계" , description = "ROLE_USER 이상의 권한이 필요함")
    @Secured({ "ROLE_USER", "ROLE_ADMIN" })
    @GetMapping("/api/v1/records/statistics")
    public ApiResponse<RecordStatisticsResponseDto> getRecordStatistics() {
        RecordStatisticsResponseDto statistics = recordService.getRecordStatistics();
        return ApiResponse.success(statistics);
    }
}
