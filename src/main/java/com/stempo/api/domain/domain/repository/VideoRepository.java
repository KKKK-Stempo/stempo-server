package com.stempo.api.domain.domain.repository;

import com.stempo.api.domain.domain.model.Video;

import java.util.List;

public interface VideoRepository {

    Video save(Video video);

    List<Video> findAll();

    Video findByIdOrThrow(Long videoId);
}
