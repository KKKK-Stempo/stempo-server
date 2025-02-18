package com.stempo.controller;

import com.stempo.dto.ApiResponse;
import com.stempo.dto.response.UserDataResponseDto;
import com.stempo.service.UserDataAggregationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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

    @Operation(summary = "[A] 사용자 전체 이용 데이터 조회", description = "ROLE_ADMIN 이상의 권한이 필요함")
    @GetMapping("/api/v1/admin/report/user-data")
    public ApiResponse<List<UserDataResponseDto>> getUserData(
        @RequestParam(name = "deviceTags") List<String> deviceTags
    ) {
        List<UserDataResponseDto> userData = userDataAggregationService.getUserData(deviceTags);
        return ApiResponse.success(userData);
    }
}
