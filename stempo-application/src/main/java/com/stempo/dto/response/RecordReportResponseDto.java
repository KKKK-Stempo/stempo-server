package com.stempo.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class RecordReportResponseDto {

    @Schema(description = "디바이스 식별자", example = "490154203237518")
    private String deviceTag;

    @Schema(description = "보행 훈련 기록", example = """
        [
            {
                "accuracy": 0.0,
                "duration": 0,
                "steps": 0,
                "leftFootAverageSpeed": 0.0,
                "rightFootAverageSpeed": 0.0,
                "bit": 4,
                "bpm": 60,
                "createdAt": "2025-01-01T00:00:00"
            }
        ]
        """)
    private List<RecordDataResponseDto> records;

    public static RecordReportResponseDto of(
        String deviceTag,
        List<RecordDataResponseDto> records
    ) {
        return RecordReportResponseDto.builder()
            .deviceTag(deviceTag)
            .records(records)
            .build();
    }
}
