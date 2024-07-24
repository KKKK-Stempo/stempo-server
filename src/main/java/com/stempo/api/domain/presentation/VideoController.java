package com.stempo.api.domain.presentation;

import com.stempo.api.domain.application.service.VideoService;
import com.stempo.api.domain.presentation.dto.request.VideoRequestDto;
import com.stempo.api.domain.presentation.dto.request.VideoUpdateRequestDto;
import com.stempo.api.domain.presentation.dto.response.VideoDetailsResponseDto;
import com.stempo.api.domain.presentation.dto.response.VideoResponseDto;
import com.stempo.api.global.common.dto.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Tag(name = "Video", description = "재활운동 영상")
public class VideoController {

    private final VideoService videoService;

    @Operation(summary = "[A] 재활운동 영상 등록", description = "ROLE_ADMIN 이상의 권한이 필요함")
    @Secured({ "ROLE_ADMIN" })
    @PostMapping("/api/v1/videos")
    public ApiResponse<Long> registerVideo(
            @Valid @RequestBody VideoRequestDto requestDto
    ) {
        Long id = videoService.registerVideo(requestDto);
        return ApiResponse.success(id);
    }

    @Operation(summary = "[U] 재활운동 영상 조회", description = "ROLE_USER 이상의 권한이 필요함")
    @Secured({ "ROLE_USER", "ROLE_ADMIN" })
    @GetMapping("/api/v1/videos")
    public ApiResponse<List<VideoResponseDto>> getVideos() {
        List<VideoResponseDto> videoResponseDtos = videoService.getVideos();
        return ApiResponse.success(videoResponseDtos);
    }

    @Operation(summary = "[U] 재활운동 영상 상세 조회", description = "ROLE_USER 이상의 권한이 필요함")
    @Secured({ "ROLE_USER", "ROLE_ADMIN" })
    @GetMapping("/api/v1/videos/{videoId}")
    public ApiResponse<VideoDetailsResponseDto> getVideo(
            @PathVariable(name = "videoId") Long videoId
    ) {
        VideoDetailsResponseDto videoResponseDto = videoService.getVideo(videoId);
        return ApiResponse.success(videoResponseDto);
    }

    @Operation(summary = "[A] 재활운동 영상 수정", description = "ROLE_ADMIN 이상의 권한이 필요함")
    @Secured({ "ROLE_ADMIN" })
    @PatchMapping("/api/v1/videos/{videoId}")
    public ApiResponse<Long> updateVideo(
            @PathVariable(name = "videoId") Long videoId,
            @Valid @RequestBody VideoUpdateRequestDto requestDto
    ) {
        Long id = videoService.updateVideo(videoId, requestDto);
        return ApiResponse.success(id);
    }

    @Operation(summary = "[A] 재활운동 영상 삭제", description = "ROLE_ADMIN 이상의 권한이 필요함")
    @Secured({ "ROLE_ADMIN" })
    @DeleteMapping("/api/v1/videos/{videoId}")
    public ApiResponse<Long> deleteVideo(
            @PathVariable Long videoId
    ) {
        Long id = videoService.deleteVideo(videoId);
        return ApiResponse.success(id);
    }
}
