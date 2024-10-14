package com.stempo.mapper;

import com.stempo.dto.response.UploadedFileResponseDto;
import com.stempo.model.UploadedFile;
import com.stempo.util.FileUtils;
import org.springframework.stereotype.Component;

@Component
public class UploadedFileDtoMapper {

    public UploadedFileResponseDto toDto(UploadedFile uploadedFile) {
        return UploadedFileResponseDto.builder()
                .originalFileName(uploadedFile.getOriginalFileName())
                .url(uploadedFile.getUrl())
                .fileSize(FileUtils.formatFileSize(uploadedFile.getFileSize()))
                .createdAt(uploadedFile.getCreatedAt())
                .build();
    }
}
