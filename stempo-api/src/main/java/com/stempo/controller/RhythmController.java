package com.stempo.controller;

import com.stempo.annotation.SuccessApiResponse;
import com.stempo.dto.ApiResponse;
import com.stempo.dto.request.RhythmRequestDto;
import com.stempo.service.RhythmService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Tag(name = "Rhythm", description = "리듬")
public class RhythmController {

    private final RhythmService rhythmService;

    @Operation(summary = "[U] 리듬 생성", description = "ROLE_USER 이상의 권한이 필요함<br>" +
            "BPM은 10 이상 200 이하의 값이어야 함<br>" +
            "Bit는 1 이상 8 이하의 값이어야 함")
    @SuccessApiResponse(data = "/resources/files/{fileName}", dataType = String.class, dataDescription = "파일 경로")
    @Secured({ "ROLE_USER", "ROLE_ADMIN" })
    @PostMapping("/api/v1/rhythm")
    public ApiResponse<String> generateRhythm(
            @Valid @RequestBody RhythmRequestDto requestDto
    ) {
        String filePath = rhythmService.createRhythm(requestDto);
        return ApiResponse.success(filePath);
    }
}
