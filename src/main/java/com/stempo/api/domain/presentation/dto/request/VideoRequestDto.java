package com.stempo.api.domain.presentation.dto.request;

import com.stempo.api.domain.domain.model.Video;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VideoRequestDto {

    @NotNull
    @Schema(description = "영상 제목", example = "리듬청각자극, 뇌성마비 환자 보행개선에 효과적 - (20120623_418회 방송)_내 몸이 찾는 음악", minLength = 1, maxLength = 100, requiredMode = Schema.RequiredMode.REQUIRED)
    private String title;

    @NotNull
    @Schema(description = "영상 내용", example = "조성래, 김수지 교수팀은 리듬청각자극이 뇌성마비 환자의 보행개선에도 효과가 있다는 연구결과를 발표했다.", minLength = 1, maxLength = 10000, requiredMode = Schema.RequiredMode.REQUIRED)
    private String content;

    @Schema(description = "썸네일 URL", example = "https://example.com/thumbnail.jpg", minLength = 1, maxLength = 255)
    private String thumbnailUrl;

    @NotNull
    @Schema(description = "영상 URL", example = "https://example.com/video.mp4", minLength = 1, maxLength = 255, requiredMode = Schema.RequiredMode.REQUIRED)
    private String videoUrl;

    public static Video toDomain(VideoRequestDto requestDto) {
        return Video.builder()
                .title(requestDto.getTitle())
                .content(requestDto.getContent())
                .thumbnailUrl(requestDto.getThumbnailUrl())
                .videoUrl(requestDto.getVideoUrl())
                .build();
    }
}
