package com.stempo.mappper;

import com.stempo.entity.UploadedFileEntity;
import com.stempo.model.UploadedFile;
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
                .createdAt(entity.getCreatedAt())
                .build();
    }
}
