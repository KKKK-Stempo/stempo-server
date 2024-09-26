package com.stempo.api.domain.application.service;

import com.stempo.api.domain.domain.model.Video;
import com.stempo.api.domain.domain.repository.VideoRepository;
import com.stempo.api.domain.presentation.dto.request.VideoRequestDto;
import com.stempo.api.domain.presentation.dto.request.VideoUpdateRequestDto;
import com.stempo.api.domain.presentation.dto.response.VideoDetailsResponseDto;
import com.stempo.api.domain.presentation.dto.response.VideoResponseDto;
import com.stempo.api.global.common.dto.PagedResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class VideoServiceImpl implements VideoService {

    private final VideoRepository repository;

    @Override
    @Transactional
    public Long registerVideo(VideoRequestDto requestDto) {
        Video video = VideoRequestDto.toDomain(requestDto);
        return repository.save(video).getId();
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResponseDto<VideoResponseDto> getVideos(Pageable pageable) {
        Page<Video> videos = repository.findAll(pageable);
        return new PagedResponseDto<>(videos.map(VideoResponseDto::toDto));
    }

    @Override
    @Transactional(readOnly = true)
    public VideoDetailsResponseDto getVideo(Long videoId) {
        Video video = repository.findByIdOrThrow(videoId);
        return VideoDetailsResponseDto.toDto(video);
    }

    @Override
    @Transactional
    public Long updateVideo(Long videoId, VideoUpdateRequestDto requestDto) {
        Video video = repository.findByIdOrThrow(videoId);
        video.update(requestDto);
        return repository.save(video).getId();
    }

    @Override
    @Transactional
    public Long deleteVideo(Long id) {
        Video video = repository.findByIdOrThrow(id);
        video.delete();
        return repository.save(video).getId();
    }
}
