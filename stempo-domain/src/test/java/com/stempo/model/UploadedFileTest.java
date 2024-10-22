package com.stempo.model;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class UploadedFileTest {

    private UploadedFile uploadedFile;

    @BeforeEach
    void setUp() {
        uploadedFile = UploadedFile.create("ORIGINAL_FILE_NAME", "SAVE_FILE_NAME", "SAVED_PATH", "URL", 100L);
    }

    @Test
    void 업로드_파일_기록이_정상적으로_생성되는지_확인한다() {
        assertThat(uploadedFile).isNotNull();
        assertThat(uploadedFile.getId()).isNull();
        assertThat(uploadedFile.getOriginalFileName()).isEqualTo("ORIGINAL_FILE_NAME");
        assertThat(uploadedFile.getSaveFileName()).isEqualTo("SAVE_FILE_NAME");
        assertThat(uploadedFile.getSavedPath()).isEqualTo("SAVED_PATH");
        assertThat(uploadedFile.getUrl()).isEqualTo("URL");
        assertThat(uploadedFile.getFileSize()).isEqualTo(100L);
        assertThat(uploadedFile.getCreatedAt()).isNull();
    }

}
