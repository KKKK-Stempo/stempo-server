package com.stempo.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class HomeworkDataResponseDto {

    @Schema(description = "과제 내용", example = "매일 스트레칭 운동 진행")
    private String description;

    @Schema(description = "완료 여부", example = "false")
    private boolean completed;

    @Schema(description = "과제 생성일", example = "2025-01-01T00:00:00")
    private LocalDateTime createdAt;

    @Schema(description = "과제 완료일", example = "2025-01-01T00:00:00")
    private LocalDateTime updatedAt;
}
