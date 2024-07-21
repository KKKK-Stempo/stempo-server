package com.stempo.api.domain.presentation.dto.request;

import com.stempo.api.domain.domain.model.Record;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RecordRequestDto {

    @NotNull(message = "Accuracy is required")
    @PositiveOrZero(message = "Accuracy must be a positive value or zero")
    @Schema(description = "정확도", example = "0.0")
    private Double accuracy;

    @PositiveOrZero(message = "Duration must be a positive value or zero")
    @Schema(description = "재활 운동 시간(초)", example = "0", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Integer duration;

    @PositiveOrZero(message = "Steps must be a positive value or zero")
    @Schema(description = "걸음 수", example = "0", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Integer steps;


    public static Record toDomain(RecordRequestDto requestDto, String deviceTag) {
        return Record.builder()
                .deviceTag(deviceTag)
                .accuracy(requestDto.getAccuracy())
                .duration(requestDto.getDuration())
                .steps(requestDto.getSteps())
                .build();
    }
}
