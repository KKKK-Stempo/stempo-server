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
    @Schema(description = "정확도", example = "0.0",
        minimum = "0.0", maximum = "100.0", requiredMode = Schema.RequiredMode.REQUIRED)
    private Double accuracy;

    @PositiveOrZero(message = "Duration must be a positive value or zero")
    @Schema(description = "재활 운동 시간(초)", example = "0")
    private Integer duration;

    @PositiveOrZero(message = "Steps must be a positive value or zero")
    @Schema(description = "걸음 수", example = "0")
    private Integer steps;

    @PositiveOrZero(message = "Left foot average speed must be a positive value or zero")
    @Schema(description = "왼발을 내딛는 평균 속도(m/s)", example = "0.0")
    private Double leftFootAvgSpeed;

    @PositiveOrZero(message = "Right foot average speed must be a positive value or zero")
    @Schema(description = "오른발을 내딛는 평균 속도(m/s)", example = "0.0")
    private Double rightFootAvgSpeed;

    @NotNull(message = "Bit is required")
    @Range(min = 1, max = 8, message = "Bit must be between 1 and 8")
    @Schema(description = "Bit", example = "4", minimum = "1", maximum = "8")
    private Integer bit;

    @NotNull(message = "BPM is required")
    @Range(min = 10, max = 200, message = "BPM must be between 10 and 200")
    @Schema(description = "BPM", example = "60", minimum = "10", maximum = "200")
    private Integer bpm;
}
