package com.stempo.api.domain.domain.repository;

import com.stempo.api.domain.domain.model.Video;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface VideoRepository {

    Video save(Video video);

    Page<Video> findAll(Pageable pageable);

    Video findByIdOrThrow(Long videoId);
}
