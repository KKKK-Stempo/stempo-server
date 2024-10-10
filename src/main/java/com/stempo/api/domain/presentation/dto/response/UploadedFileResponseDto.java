package com.stempo.api.domain.presentation.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class UploadedFileResponseDto {

    private String originalFileName;
    private String url;
    private String fileSize;
    private LocalDateTime createdAt;
}
