package com.stempo.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.stempo.entity.UploadedFileEntity;
import com.stempo.model.UploadedFile;
import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class UploadedFileMapperTest {

    private UploadedFileMapper uploadedFileMapper;

    @BeforeEach
    void setUp() {
        uploadedFileMapper = new UploadedFileMapper();
    }

    @Test
    void 도메인을_엔티티로_매핑한다() {
        // given
        UploadedFile uploadedFile = UploadedFile.builder()
                .id(1L)
                .originalFileName("testFile.txt")
                .saveFileName("savedTestFile.txt")
                .savedPath("/uploads/files")
                .url("http://localhost/uploads/files/savedTestFile.txt")
                .fileSize(1024L)
                .createdAt(LocalDateTime.now())
                .build();

        // when
        UploadedFileEntity entity = uploadedFileMapper.toEntity(uploadedFile);

        // then
        assertThat(entity.getId()).isEqualTo(uploadedFile.getId());
        assertThat(entity.getOriginalFileName()).isEqualTo(uploadedFile.getOriginalFileName());
        assertThat(entity.getSaveFileName()).isEqualTo(uploadedFile.getSaveFileName());
        assertThat(entity.getSavedPath()).isEqualTo(uploadedFile.getSavedPath());
        assertThat(entity.getUrl()).isEqualTo(uploadedFile.getUrl());
        assertThat(entity.getFileSize()).isEqualTo(uploadedFile.getFileSize());
    }

    @Test
    void 엔티티를_도메인으로_매핑한다() {
        // given
        UploadedFileEntity entity = UploadedFileEntity.builder()
                .id(1L)
                .originalFileName("testFile.txt")
                .saveFileName("savedTestFile.txt")
                .savedPath("/uploads/files")
                .url("http://localhost/uploads/files/savedTestFile.txt")
                .fileSize(1024L)
                .build();
        entity.setCreatedAt(LocalDateTime.now());

        // when
        UploadedFile uploadedFile = uploadedFileMapper.toDomain(entity);

        // then
        assertThat(uploadedFile.getId()).isEqualTo(entity.getId());
        assertThat(uploadedFile.getOriginalFileName()).isEqualTo(entity.getOriginalFileName());
        assertThat(uploadedFile.getSaveFileName()).isEqualTo(entity.getSaveFileName());
        assertThat(uploadedFile.getSavedPath()).isEqualTo(entity.getSavedPath());
        assertThat(uploadedFile.getUrl()).isEqualTo(entity.getUrl());
        assertThat(uploadedFile.getFileSize()).isEqualTo(entity.getFileSize());
        assertThat(uploadedFile.getCreatedAt()).isEqualTo(entity.getCreatedAt());
    }
}
