package com.stempo.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class RecordResponseDto {

    @Schema(description = "정확도 평균", example = "0")
    private Integer accuracyAverage;

    @Schema(description = "보행 훈련 기록", example = """
        [
            {
                "accuracy": 0.0,
                "duration": 0,
                "steps": 0,
                "date": "2025-01-01"
            }
        ]
        """)
    private List<RecordItemDto> records;
}
