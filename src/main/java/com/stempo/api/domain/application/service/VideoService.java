package com.stempo.api.domain.application.service;

import com.stempo.api.domain.presentation.dto.request.VideoRequestDto;
import com.stempo.api.domain.presentation.dto.request.VideoUpdateRequestDto;
import com.stempo.api.domain.presentation.dto.response.VideoResponseDto;

import java.util.List;

public interface VideoService {

    Long registerVideo(VideoRequestDto requestDto);

    List<VideoResponseDto> getVideos();

    Long updateVideo(Long videoId, VideoUpdateRequestDto requestDto);

    Long deleteVideo(Long id);
}
