package com.stempo.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class HomeworkUpdateRequestDto {

    @Schema(description = "과제 내용", example = "매일 스트레칭 운동 진행")
    private String description;

    @Schema(description = "완료 여부", example = "true")
    private Boolean completed;
}
