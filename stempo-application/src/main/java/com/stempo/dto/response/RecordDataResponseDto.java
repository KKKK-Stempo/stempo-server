package com.stempo.dto.response;

import com.stempo.dto.DecryptedRecord;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class RecordDataResponseDto {

    @Schema(description = "정확도", example = "0.0")
    private Double accuracy;

    @Schema(description = "보행 훈련 시간(초)", example = "0")
    private Integer duration;

    @Schema(description = "걸음 수", example = "0")
    private Integer steps;

    @Schema(description = "왼발을 내딛는 평균 속도(m/s)", example = "0.0")
    private Double leftFootAverageSpeed;

    @Schema(description = "오른발을 내딛는 평균 속도(m/s)", example = "0.0")
    private Double rightFootAverageSpeed;

    @Schema(description = "보행 훈련에 사용된 리듬의 Bit", example = "4", minimum = "1", maximum = "8")
    private Integer bit;

    @Schema(description = "보행 훈련에 사용된 리듬의 BPM", example = "60", minimum = "10", maximum = "200")
    private Integer bpm;

    @Schema(description = "보행 훈련 시행일시", example = "2025-01-01T00:00:00")
    private LocalDateTime createdAt;

    public static RecordDataResponseDto from(DecryptedRecord decryptedRecord) {
        return RecordDataResponseDto.builder()
            .accuracy(decryptedRecord.getAccuracy())
            .duration(decryptedRecord.getDuration())
            .steps(decryptedRecord.getSteps())
            .leftFootAverageSpeed(decryptedRecord.getLeftFootAverageSpeed())
            .rightFootAverageSpeed(decryptedRecord.getRightFootAverageSpeed())
            .bit(decryptedRecord.getBit())
            .bpm(decryptedRecord.getBpm())
            .createdAt(decryptedRecord.getCreatedAt())
            .build();
    }
}
