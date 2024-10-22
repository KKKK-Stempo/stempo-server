package com.stempo.util;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.stempo.exception.FilePermissionException;
import com.stempo.exception.InvalidFileAttributeException;
import com.stempo.exception.InvalidFileNameException;
import java.io.File;
import java.io.IOException;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class FileUtilsTest {

    @TempDir
    Path tempDir;

    @Test
    void 파일_경로가_정상적으로_검증된다() {
        // given
        String filePath = "test_file.txt";

        // when
        Path resolvedPath = FileUtils.validateFilePath(filePath, tempDir.toString());

        // then
        assertThat(resolvedPath).isNotNull();
    }

    @Test
    void 파일_경로가_기본_디렉토리_내부에_포함되지_않으면_예외가_발생한다() {
        // given
        String filePath = "../../invalid_file.txt";

        // when, then
        assertThatThrownBy(() -> FileUtils.validateFilePath(filePath, tempDir.toString()))
                .isInstanceOf(InvalidPathException.class)
                .hasMessageContaining("Invalid file path: Path traversal detected.");
    }

    @Test
    void 파일이_존재하면_정상적으로_검증된다() throws IOException {
        // given
        File existingFile = new File(tempDir.toFile(), "existing_file.txt");
        existingFile.createNewFile();

        // when
        FileUtils.validateFileExists(existingFile.toPath());

        // then
        assertThat(existingFile.exists()).isTrue();
    }

    @Test
    void 파일이_존재하지_않으면_에러가_발생한다() {
        // given
        Path invalidPath = tempDir.resolve("nonexistent_file.txt");

        // when, then
        assertThatThrownBy(() -> FileUtils.validateFileExists(invalidPath))
                .isInstanceOf(InvalidPathException.class)
                .hasMessageContaining("File does not exist");
    }

    @Test
    void 고유한_파일명이_정상적으로_생성된다() {
        // given
        String extension = "txt";

        // when
        String fileName = FileUtils.makeFileName(extension);

        // then
        assertThat(fileName).endsWith(".txt");
    }

    @Test
    void 디렉토리가_존재하지_않으면_정상적으로_생성된다() {
        // given
        File newDirectory = new File(tempDir.toFile(), "new_directory");

        // when
        FileUtils.ensureParentDirectoryExists(newDirectory, tempDir.toString());

        // then
        assertThat(newDirectory.getParentFile().exists()).isTrue();
    }

    @Test
    void 부모_디렉토리_생성에_실패하면_예외가_발생한다() {
        // given
        String invalidDirectoryPath = "/root/invalid_directory";
        File invalidDir = new File(invalidDirectoryPath);

        // when, then
        assertThatThrownBy(() -> FileUtils.ensureParentDirectoryExists(invalidDir, tempDir.toString()))
                .isInstanceOf(InvalidPathException.class)
                .hasMessageContaining("Invalid file path");
    }

    @Test
    void 잘못된_경로로_디렉토리_생성에_실패한다() {
        // given
        String invalidDirectoryPath = "../../invalid_directory";
        File invalidDir = new File(tempDir.toFile(), invalidDirectoryPath);

        // when, then
        assertThatThrownBy(() -> FileUtils.ensureParentDirectoryExists(invalidDir, tempDir.toString()))
                .isInstanceOf(InvalidPathException.class)
                .hasMessageContaining("Invalid file path: Path traversal detected.");
    }

    @Test
    void 파일_확장자와_속성이_유효하면_정상적으로_검증된다() {
        // given
        String fileName = "valid_file.txt";
        Set<String> disallowedExtensions = Set.of("exe", "bat");

        // when, then
        FileUtils.validateFileAttributes(fileName, disallowedExtensions);
    }

    @Test
    void 허용되지_않은_확장자가_있으면_예외가_발생한다() {
        // given
        String fileName = "malicious_file.exe";
        Set<String> disallowedExtensions = Set.of("exe", "bat");

        // when, then
        assertThatThrownBy(() -> FileUtils.validateFileAttributes(fileName, disallowedExtensions))
                .isInstanceOf(InvalidFileAttributeException.class)
                .hasMessageContaining("Invalid file extension");
    }

    @Test
    void 잘못된_파일명_검증() {
        // given
        String invalidFileName = "../invalid.txt";

        // when, then
        assertThatThrownBy(() -> FileUtils.validateFilename(invalidFileName))
                .isInstanceOf(InvalidFileNameException.class)
                .hasMessageContaining("Invalid file name");
    }

    @Test
    void 유효한_파일명_검증() {
        // given
        String validFileName = "valid.txt";

        // when, then
        FileUtils.validateFilename(validFileName);
    }

    @Test
    void 확장자가_허용되지_않은_경우_유효하지_않다() {
        // given
        String extension = "exe";
        Set<String> disallowedExtensions = Set.of("exe", "bat");

        // when, then
        assertThat(FileUtils.validateExtension(extension, disallowedExtensions)).isFalse();
    }

    @Test
    void 확장자가_허용된_경우_유효하다() {
        // given
        String extension = "txt";
        Set<String> disallowedExtensions = Set.of("exe", "bat");

        // when, then
        assertThat(FileUtils.validateExtension(extension, disallowedExtensions)).isTrue();
    }

    @Test
    void 파일_읽기_전용_권한이_정상적으로_설정된다() throws IOException {
        // given
        File readOnlyFile = new File(tempDir.toFile(), "readonly_file.txt");
        readOnlyFile.createNewFile();

        // when
        FileUtils.setFilePermissions(readOnlyFile, readOnlyFile.getAbsolutePath(), tempDir.toString());

        // then
        assertThat(readOnlyFile.canRead()).isTrue();
        assertThat(readOnlyFile.canWrite()).isFalse();
        assertThat(readOnlyFile.canExecute()).isFalse();
    }

    @Test
    void 잘못된_경로로_파일_읽기_전용_권한_설정에_실패한다() {
        // given
        File invalidFile = new File(tempDir.toFile(), "../../invalid_file.txt");

        // when, then
        assertThatThrownBy(
                () -> FileUtils.setFilePermissions(invalidFile, invalidFile.getAbsolutePath(), tempDir.toString()))
                .isInstanceOf(FilePermissionException.class)
                .hasMessageContaining("Failed to set file permissions");
    }

    @Test
    void 파일_크기가_정상적으로_포맷된다() {
        // given
        long fileSizeInBytes = 1024 * 1024;

        // when
        String formattedSize = FileUtils.formatFileSize(fileSizeInBytes);

        // then
        assertThat(formattedSize).isEqualTo("1.00MB");
    }
}
