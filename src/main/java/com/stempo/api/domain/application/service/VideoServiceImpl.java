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

    private final VideoRepository videoRepository;

    @Override
    public Long registerVideo(VideoRequestDto requestDto) {
        Video video = VideoRequestDto.toDomain(requestDto);
        return videoRepository.save(video).getId();
    }

    @Override
    public List<VideoResponseDto> getVideos() {
        List<Video> videos = videoRepository.findAll();
        return videos.stream()
                .map(VideoResponseDto::toDto)
                .toList();
    }

    @Override
    public Long updateVideo(Long videoId, VideoUpdateRequestDto requestDto) {
        Video video = videoRepository.findByIdOrThrow(videoId);
        video.update(requestDto);
        return videoRepository.save(video).getId();
    }

    @Override
    public Long deleteVideo(Long id) {
        Video video = videoRepository.findByIdOrThrow(id);
        video.delete();
        return videoRepository.save(video).getId();
    }
}
