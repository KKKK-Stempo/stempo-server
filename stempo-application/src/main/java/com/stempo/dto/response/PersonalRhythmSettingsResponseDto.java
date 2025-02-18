package com.stempo.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PersonalRhythmSettingsResponseDto {

    @Schema(description = "디바이스 식별자", example = "490154203237518")
    private String deviceTag;

    @Schema(description = "온보딩 추천 리듬 Bit", example = "4")
    private Integer onboardingBit;

    @Schema(description = "온보딩 추천 리듬 BPM", example = "60")
    private Integer onboardingBpm;

    @Schema(description = "마지막 보행 훈련 리듬 Bit", example = "6")
    private Integer lastRecordBit;

    @Schema(description = "마지막 보행 훈련 리듬 BPM", example = "80")
    private Integer lastRecordBpm;

    public static PersonalRhythmSettingsResponseDto create(String deviceTag) {
        return PersonalRhythmSettingsResponseDto.builder()
            .deviceTag(deviceTag)
            .onboardingBit(0)
            .onboardingBpm(0)
            .lastRecordBit(0)
            .lastRecordBpm(0)
            .build();
    }

    public static PersonalRhythmSettingsResponseDto of(
        String deviceTag,
        Integer onboardingBit,
        Integer onboardingBpm,
        Integer lastRecordBit,
        Integer lastRecordBpm
    ) {
        return PersonalRhythmSettingsResponseDto.builder()
            .deviceTag(deviceTag)
            .onboardingBit(onboardingBit)
            .onboardingBpm(onboardingBpm)
            .lastRecordBit(lastRecordBit)
            .lastRecordBpm(lastRecordBpm)
            .build();
    }
}
