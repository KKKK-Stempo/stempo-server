package com.stempo.api.domain.persistence.repository;

import com.stempo.api.domain.domain.model.Video;
import com.stempo.api.domain.domain.repository.VideoRepository;
import com.stempo.api.domain.persistence.entity.VideoEntity;
import com.stempo.api.domain.persistence.mappper.VideoMapper;
import com.stempo.api.global.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class VideoRepositoryImpl implements VideoRepository {

    private final VideoJpaRepository repository;
    private final VideoMapper mapper;

    @Override
    public Video save(Video video) {
        VideoEntity jpaEntity = mapper.toEntity(video);
        VideoEntity savedEntity = repository.save(jpaEntity);
        return mapper.toDomain(savedEntity);
    }

    @Override
    public Page<Video> findAll(Pageable pageable) {
        Page<VideoEntity> videos = repository.findAllVideos(pageable);
        return videos.map(mapper::toDomain);
    }

    @Override
    public Video findByIdOrThrow(Long videoId) {
        return repository.findByIdAndNotDeleted(videoId)
                .map(mapper::toDomain)
                .orElseThrow(() -> new NotFoundException("[Video] id: " + videoId + " not found"));
    }
}
