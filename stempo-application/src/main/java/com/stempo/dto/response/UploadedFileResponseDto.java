package com.stempo.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class UploadedFileResponseDto {

    @Schema(description = "파일 원본 이름", example = "image.jpg")
    private String originalFileName;

    @Schema(description = "파일 접근 URL", example = "/resources/files/947051880039041_19dea234-b6ec-4c4b-bc92-c53c0d921943.jpg")
    private String url;

    @Schema(description = "파일 크기", example = "1.0MB")
    private String fileSize;

    @Schema(description = "파일 업로드 일시", example = "2024-01-01T00:00:00")
    private LocalDateTime createdAt;
}
