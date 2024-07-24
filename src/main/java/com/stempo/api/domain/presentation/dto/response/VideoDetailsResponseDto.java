package com.stempo.api.domain.presentation.dto.response;

import com.stempo.api.domain.domain.model.Video;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class VideoDetailsResponseDto {

    private final Long id;
    private final String title;
    private final String content;
    private final String thumbnailUrl;
    private final String videoUrl;
    private final String createdAt;

    public static VideoDetailsResponseDto toDto(Video video) {
        return VideoDetailsResponseDto.builder()
                .id(video.getId())
                .title(video.getTitle())
                .content(video.getContent())
                .thumbnailUrl(video.getThumbnailUrl())
                .videoUrl(video.getVideoUrl())
                .createdAt(video.getCreatedAt().toString())
                .build();
    }
}
