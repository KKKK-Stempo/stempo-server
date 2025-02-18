package com.stempo.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PersonalTrainingSettingsResponseDto {

    @Schema(description = "디바이스 식별자", example = "490154203237518")
    private String deviceTag;

    @Schema(description = "사용자의 첫 번째 보행 훈련 데이터 (초기 분석 지표)", example = """
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
        """)
    private RecordDataResponseDto initialTraining;

    @Schema(description = "사용자에게 부여된 과제 데이터", example = """
        [
          {
            "description": "매일 스트레칭 운동 진행",
            "completed": false,
            "createdAt": "2025-01-01T00:00:00",
            "updatedAt": "2025-01-01T00:00:00"
          }
        ]
        """)
    private List<HomeworkDataResponseDto> homeworks;

    public static PersonalTrainingSettingsResponseDto of(
        String deviceTag,
        RecordDataResponseDto initialTraining,
        List<HomeworkDataResponseDto> homeworks
    ) {
        return PersonalTrainingSettingsResponseDto.builder()
            .deviceTag(deviceTag)
            .initialTraining(initialTraining)
            .homeworks(homeworks)
            .build();
    }
}
