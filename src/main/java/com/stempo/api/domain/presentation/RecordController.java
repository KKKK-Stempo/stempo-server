package com.stempo.api.domain.presentation;

import com.stempo.api.domain.application.service.RecordService;
import com.stempo.api.domain.presentation.dto.request.RecordRequestDto;
import com.stempo.api.domain.presentation.dto.response.RecordResponseDto;
import com.stempo.api.global.common.dto.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Tag(name = "Record", description = "기록")
public class RecordController {

    private final RecordService recordService;

    @Operation(summary = "[U] 재활 운동 기록", description = "ROLE_USER 이상의 권한이 필요함")
    @PostMapping("/api/v1/records")
    public ApiResponse<String> record(
            @Valid @RequestBody RecordRequestDto requestDto
    ) {
        String deviceTag = recordService.record(requestDto);
        return ApiResponse.success(deviceTag);
    }

    @Operation(summary = "[U] 내 재활 운동 기록 조회", description = "ROLE_USER 이상의 권한이 필요함")
    @GetMapping("/api/v1/records")
    public ApiResponse<List<RecordResponseDto>> getRecords(
            @RequestParam(name = "startDate") LocalDate startDate,
            @RequestParam(name = "endDate") LocalDate endDate
    ) {
        List<RecordResponseDto> records = recordService.getRecordsByDateRange(startDate, endDate);
        return ApiResponse.success(records);
    }
}
