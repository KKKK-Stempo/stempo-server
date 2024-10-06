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
    private String fileSize;
    private LocalDateTime createdAt;

    public static UploadedFileResponseDto toDto(UploadedFile uploadedFile) {
        return UploadedFileResponseDto.builder()
                .originalFileName(uploadedFile.getOriginalFileName())
                .url(uploadedFile.getUrl())
                .fileSize(formatFileSize(uploadedFile.getFileSize()))
                .createdAt(uploadedFile.getCreatedAt())
                .build();
    }

    private static String formatFileSize(long fileSize) {
        final long KB = 1024;
        final long MB = KB * 1024;
        final long GB = MB * 1024;

        if (fileSize < KB) {
            return fileSize + " B";
        } else if (fileSize < MB) {
            return String.format("%.2fKB", fileSize / (double) KB);
        } else if (fileSize < GB) {
            return String.format("%.2fMB", fileSize / (double) MB);
        } else {
            return String.format("%.2fGB", fileSize / (double) GB);
        }
    }
}
