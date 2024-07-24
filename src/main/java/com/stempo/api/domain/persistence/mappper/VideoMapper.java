package com.stempo.api.domain.persistence.mappper;

import com.stempo.api.domain.domain.model.Video;
import com.stempo.api.domain.persistence.entity.VideoEntity;
import org.springframework.stereotype.Component;

@Component
public class VideoMapper {

    public VideoEntity toEntity(Video video) {
        return VideoEntity.builder()
                .title(video.getTitle())
                .content(video.getContent())
                .thumbnailUrl(video.getThumbnailUrl())
                .videoUrl(video.getVideoUrl())
                .deleted(video.isDeleted())
                .build();
    }

    public Video toDomain(VideoEntity entity) {
        return Video.builder()
                .title(entity.getTitle())
                .content(entity.getContent())
                .thumbnailUrl(entity.getThumbnailUrl())
                .videoUrl(entity.getVideoUrl())
                .deleted(entity.isDeleted())
                .createdAt(entity.getCreatedAt())
                .build();
    }
}
