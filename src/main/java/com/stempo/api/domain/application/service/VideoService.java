package com.stempo.api.domain.application.service;

import com.stempo.api.domain.presentation.dto.request.VideoRequestDto;
import com.stempo.api.domain.presentation.dto.request.VideoUpdateRequestDto;
import com.stempo.api.domain.presentation.dto.response.VideoDetailsResponseDto;
import com.stempo.api.domain.presentation.dto.response.VideoResponseDto;
import com.stempo.api.global.common.dto.PagedResponseDto;
import org.springframework.data.domain.Pageable;

public interface VideoService {

    Long registerVideo(VideoRequestDto requestDto);

    PagedResponseDto<VideoResponseDto> getVideos(Pageable pageable);

    VideoDetailsResponseDto getVideo(Long videoId);

    Long updateVideo(Long videoId, VideoUpdateRequestDto requestDto);

    Long deleteVideo(Long id);
}
