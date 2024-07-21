package com.stempo.api.domain.persistence.mappper;

import com.stempo.api.domain.domain.model.UploadedFile;
import com.stempo.api.domain.persistence.entity.UploadedFileEntity;
import org.springframework.stereotype.Component;

@Component
public class UploadedFileMapper {

    public UploadedFileEntity toEntity(UploadedFile uploadedFile) {
        return UploadedFileEntity.builder()
                .id(uploadedFile.getId())
                .originalFileName(uploadedFile.getOriginalFileName())
                .saveFileName(uploadedFile.getSaveFileName())
                .savedPath(uploadedFile.getSavedPath())
                .url(uploadedFile.getUrl())
                .fileSize(uploadedFile.getFileSize())
                .contentType(uploadedFile.getContentType())
                .build();
    }

    public UploadedFile toDomain(UploadedFileEntity entity) {
        return UploadedFile.builder()
                .id(entity.getId())
                .originalFileName(entity.getOriginalFileName())
                .saveFileName(entity.getSaveFileName())
                .savedPath(entity.getSavedPath())
                .url(entity.getUrl())
                .fileSize(entity.getFileSize())
                .contentType(entity.getContentType())
                .createdAt(entity.getCreatedAt())
                .build();
    }
}
