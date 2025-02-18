package com.stempo.dto.response;

import com.stempo.dto.DecryptedRecord;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class RhythmDataResponseDto {

    @Schema(description = "보행 훈련에 사용된 리듬의 Bit", example = "4", minimum = "1", maximum = "8")
    private Integer bit;

    @Schema(description = "보행 훈련에 사용된 리듬의 BPM", example = "60", minimum = "10", maximum = "200")
    private Integer bpm;

    @Schema(description = "보행 훈련 시행일시", example = "2025-01-01T00:00:00")
    private LocalDateTime createdAt;

    public static RhythmDataResponseDto from(DecryptedRecord decryptedRecord) {
        return RhythmDataResponseDto.builder()
            .bit(decryptedRecord.getBit())
            .bpm(decryptedRecord.getBpm())
            .createdAt(decryptedRecord.getCreatedAt())
            .build();
    }
}
