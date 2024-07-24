package com.stempo.api.domain.domain.model;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class UploadedFile {

    private Long id;
    private String originalFileName;
    private String saveFileName;
    private String savedPath;
    private String url;
    private Long fileSize;
    private LocalDateTime createdAt;

    public static UploadedFile create(String originalFileName, String saveFileName, String savedPath, String url, Long fileSize) {
        return UploadedFile.builder()
                .originalFileName(originalFileName)
                .saveFileName(saveFileName)
                .savedPath(savedPath)
                .url(url)
                .fileSize(fileSize)
                .build();
    }
}
