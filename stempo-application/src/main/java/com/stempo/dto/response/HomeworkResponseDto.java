package com.stempo.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class HomeworkResponseDto {

    @Schema(description = "과제 ID", example = "1")
    private Long id;

    @Schema(description = "과제 내용", example = "매일 스트레칭 운동 진행")
    private String description;

    @Schema(description = "완료 여부", example = "false")
    private boolean completed;
}
