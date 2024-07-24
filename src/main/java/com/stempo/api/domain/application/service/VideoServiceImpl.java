package com.stempo.api.domain.application.service;

import com.stempo.api.domain.domain.model.Video;
import com.stempo.api.domain.domain.repository.VideoRepository;
import com.stempo.api.domain.presentation.dto.request.VideoRequestDto;
import com.stempo.api.domain.presentation.dto.request.VideoUpdateRequestDto;
import com.stempo.api.domain.presentation.dto.response.VideoResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class VideoServiceImpl implements VideoService {

    private final VideoRepository repository;

    @Override
    public Long registerVideo(VideoRequestDto requestDto) {
        Video video = VideoRequestDto.toDomain(requestDto);
        return repository.save(video).getId();
    }

    @Override
    public List<VideoResponseDto> getVideos() {
        List<Video> videos = repository.findAll();
        return videos.stream()
                .map(VideoResponseDto::toDto)
                .toList();
    }

    @Override
    public Long updateVideo(Long videoId, VideoUpdateRequestDto requestDto) {
        Video video = repository.findByIdOrThrow(videoId);
        video.update(requestDto);
        return repository.save(video).getId();
    }

    @Override
    public Long deleteVideo(Long id) {
        Video video = repository.findByIdOrThrow(id);
        video.delete();
        return repository.save(video).getId();
    }
}
