package com.stempo.api.domain.presentation.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Range;

@Getter
@Setter
public class RhythmRequestDto {

    @NotNull(message = "BPM is required")
    @Range(min = 10, max = 200, message = "BPM must be between 10 and 200")
    @Schema(description = "BPM", example = "60", minimum = "10", maximum = "200")
    private Integer bpm;

    @NotNull(message = "Bit is required")
    @Range(min = 1, max = 8, message = "Bit must be between 1 and 8")
    @Schema(description = "Bit", example = "4", minimum = "1", maximum = "8")
    private Integer bit;
}
