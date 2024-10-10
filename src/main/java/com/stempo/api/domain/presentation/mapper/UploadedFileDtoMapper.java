package com.stempo.api.domain.presentation.mapper;

import com.stempo.api.domain.domain.model.UploadedFile;
import com.stempo.api.domain.presentation.dto.response.UploadedFileResponseDto;
import com.stempo.api.global.util.FileUtil;
import org.springframework.stereotype.Component;

@Component
public class UploadedFileDtoMapper {

    public UploadedFileResponseDto toDto(UploadedFile uploadedFile) {
        return UploadedFileResponseDto.builder()
                .originalFileName(uploadedFile.getOriginalFileName())
                .url(uploadedFile.getUrl())
                .fileSize(FileUtil.formatFileSize(uploadedFile.getFileSize()))
                .createdAt(uploadedFile.getCreatedAt())
                .build();
    }
}
