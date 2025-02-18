package com.stempo.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class RhythmReportResponseDto {

    @Schema(description = "디바이스 식별자", example = "490154203237518")
    private String deviceTag;

    @Schema(description = "보행 훈련에 사용된 리듬 데이터", example = """
        [
            {
                "bit": 4,
                "bpm": 60,
                "createdAt": "2025-01-01T00:00:00"
            }
        ]
        """)
    private List<RhythmDataResponseDto> records;

    public static RhythmReportResponseDto of(
        String deviceTag,
        List<RhythmDataResponseDto> records
    ) {
        return RhythmReportResponseDto.builder()
            .deviceTag(deviceTag)
            .records(records)
            .build();
    }
}
