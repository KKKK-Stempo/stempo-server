package com.stempo.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Range;

@Getter
@Setter
public class RecordRequestDto {

    @NotNull(message = "Accuracy is required")
    @Range(min = 0, max = 100, message = "Accuracy must be between 0 and 100")
    @Schema(description = "정확도", example = "0.0", minimum = "0.0", maximum = "100.0", requiredMode = Schema.RequiredMode.REQUIRED)
    private Double accuracy;

    @PositiveOrZero(message = "Duration must be a positive value or zero")
    @Schema(description = "재활 운동 시간(초)", example = "0")
    private Integer duration;

    @PositiveOrZero(message = "Steps must be a positive value or zero")
    @Schema(description = "걸음 수", example = "0")
    private Integer steps;
}
