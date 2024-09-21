package com.stempo.api.domain.presentation.dto.request;

import com.stempo.api.domain.domain.model.Homework;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class HomeworkRequestDto {

    @NotBlank(message = "Description is required")
    @Schema(description = "과제 내용", example = "매일 스트레칭 운동 진행", requiredMode = Schema.RequiredMode.REQUIRED)
    private String description;

    public static Homework toDomain(HomeworkRequestDto request, String deviceTag) {
        return Homework.builder()
                .deviceTag(deviceTag)
                .description(request.getDescription())
                .completed(false)
                .build();
    }
}
