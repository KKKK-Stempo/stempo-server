package com.stempo.api.domain.presentation.dto.response;

import com.stempo.api.domain.domain.model.UploadedFile;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class UploadedFileResponseDto {

    private String originalFileName;
    private String url;
    private Long fileSize;
    private LocalDateTime createdAt;

    public static UploadedFileResponseDto toDto(UploadedFile uploadedFile) {
        return UploadedFileResponseDto.builder()
                .originalFileName(uploadedFile.getOriginalFileName())
                .url(uploadedFile.getUrl())
                .fileSize(uploadedFile.getFileSize())
                .createdAt(uploadedFile.getCreatedAt())
                .build();
    }
}
