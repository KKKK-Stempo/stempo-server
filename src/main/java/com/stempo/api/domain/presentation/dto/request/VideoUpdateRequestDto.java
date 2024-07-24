package com.stempo.api.domain.presentation.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VideoUpdateRequestDto {

    @Schema(description = "영상 제목", example = "영상 제목", minLength = 1, maxLength = 100)
    private String title;

    @Schema(description = "영상 내용", example = "영상 내용", minLength = 1, maxLength = 10000)
    private String content;

    @Schema(description = "썸네일 URL", example = "https://example.com/thumbnail.jpg", minLength = 1, maxLength = 255)
    private String thumbnailUrl;

    @Schema(description = "영상 URL", example = "https://example.com/video.mp4", minLength = 1, maxLength = 255)
    private String videoUrl;
}
