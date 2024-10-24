package com.stempo.util;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.stempo.exception.InvalidFileAttributeException;
import com.stempo.exception.InvalidFileNameException;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

@ExtendWith(MockitoExtension.class)
class FileHandlerTest {

    @TempDir
    Path tempDir;
    private FileHandler fileHandler;

    @BeforeEach
    void setUp() {
        fileHandler = new FileHandler(
                new String[]{"exe", "bat"},
                tempDir.toString()
        );
        fileHandler.init();
    }

    @Test
    void 파일을_성공적으로_저장한다() throws IOException {
        // given
        MultipartFile multipartFile = new MockMultipartFile(
                "file",
                "test.txt",
                "text/plain",
                "Sample content".getBytes()
        );

        String category = "docs";

        // when
        String savedPath = fileHandler.saveFile(multipartFile, category);

        // then
        assertThat(savedPath).isNotNull();
        assertThat(savedPath).contains(tempDir.toString() + File.separator + category);
        File savedFile = new File(savedPath);
        assertThat(savedFile.exists()).isTrue();
    }

    @Test
    void 파일객체를_성공적으로_저장한다() throws IOException {
        // given
        File file = new File(tempDir.toFile(), "test.txt");
        Files.writeString(file.toPath(), "Sample content");

        // when
        String savedPath = fileHandler.saveFile(file);

        // then
        assertThat(savedPath).isNotNull();
        File savedFile = new File(savedPath);
        assertThat(savedFile.exists()).isTrue();
        assertThat(Files.readString(savedFile.toPath())).isEqualTo("Sample content");
    }

    @Test
    void 파일명에_잘못된_문자열이_포함되어_있을_경우_예외가_발생한다() {
        // given
        MultipartFile multipartFile = new MockMultipartFile(
                "file",
                "test..txt",
                "text/plain",
                "Sample content".getBytes()
        );

        String category = "docs";

        // then
        assertThatThrownBy(() -> fileHandler.saveFile(multipartFile, category))
                .isInstanceOf(InvalidFileNameException.class)
                .hasMessageContaining("Invalid file name");
    }

    @Test
    void 잘못된_확장자를_가진_파일을_저장하려_할_때_예외가_발생한다() {
        // given
        MultipartFile multipartFile = new MockMultipartFile(
                "file",
                "test.exe",
                "application/octet-stream",
                "Sample content".getBytes()
        );

        String category = "docs";

        // then
        assertThatThrownBy(() -> fileHandler.saveFile(multipartFile, category))
                .isInstanceOf(InvalidFileAttributeException.class)
                .hasMessageContaining("Invalid file extension");
    }

    @Test
    void 파일을_성공적으로_삭제한다() throws IOException {
        // given
        MultipartFile multipartFile = new MockMultipartFile(
                "file",
                "test.txt",
                "text/plain",
                "Sample content".getBytes()
        );

        String category = "docs";
        String savedPath = fileHandler.saveFile(multipartFile, category);

        // when
        boolean isDeleted = fileHandler.deleteFile(savedPath);

        // then
        assertThat(isDeleted).isTrue();
        File savedFile = new File(savedPath);
        assertThat(savedFile.exists()).isFalse();
    }

    @Test
    void 삭제할_파일이_존재하지_않을_경우_삭제_실패() {
        // given
        String savedPath = tempDir.resolve("non-existent-file.txt").toString();

        // when
        boolean isDeleted = fileHandler.deleteFile(savedPath);

        // then
        assertThat(isDeleted).isFalse();
    }
}
