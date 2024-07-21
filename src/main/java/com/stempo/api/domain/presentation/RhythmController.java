package com.stempo.api.domain.presentation;

import com.stempo.api.domain.application.service.RhythmService;
import com.stempo.api.global.common.dto.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Tag(name = "Rhythm", description = "리듬")
public class RhythmController {

    private final RhythmService rhythmService;

    @Operation(summary = "[U] 리듬 생성", description = "ROLE_USER 이상의 권한이 필요함")
    @Secured({ "ROLE_USER", "ROLE_ADMIN" })
    @PostMapping("/api/v1/rhythm/{bpm}")
    public ApiResponse<String> generateRhythm(
            @PathVariable(name = "bpm") int bpm
    ) {
        String filePath = rhythmService.createRhythm(bpm);
        return ApiResponse.success(filePath);
    }
}
