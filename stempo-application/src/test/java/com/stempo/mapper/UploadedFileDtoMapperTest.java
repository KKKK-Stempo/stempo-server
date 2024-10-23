package com.stempo.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.stempo.dto.response.UploadedFileResponseDto;
import com.stempo.model.UploadedFile;
import com.stempo.util.FileUtils;
import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class UploadedFileDtoMapperTest {

    private UploadedFileDtoMapper uploadedFileDtoMapper;

    @BeforeEach
    void setUp() {
        uploadedFileDtoMapper = new UploadedFileDtoMapper();
    }

    @Test
    void UploadedFile을_UploadedFileResponseDto로_매핑한다() {
        // given
        UploadedFile uploadedFile = UploadedFile.builder()
                .originalFileName("testFile.png")
                .url("/resources/files/testFile.png")
                .fileSize(204800L)
                .createdAt(LocalDateTime.of(2023, 10, 24, 12, 0))
                .build();

        String expectedFileSize = FileUtils.formatFileSize(204800L);

        // when
        UploadedFileResponseDto responseDto = uploadedFileDtoMapper.toDto(uploadedFile);

        // then
        assertThat(responseDto.getOriginalFileName()).isEqualTo("testFile.png");
        assertThat(responseDto.getUrl()).isEqualTo("/resources/files/testFile.png");
        assertThat(responseDto.getFileSize()).isEqualTo(expectedFileSize);
        assertThat(responseDto.getCreatedAt()).isEqualTo(LocalDateTime.of(2023, 10, 24, 12, 0));
    }
}
