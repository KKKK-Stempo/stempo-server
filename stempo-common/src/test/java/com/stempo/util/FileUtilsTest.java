package com.stempo.util;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.stempo.exception.BaseException;
import com.stempo.exception.ErrorCode;
import java.io.File;
import java.io.IOException;
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
            .isInstanceOf(BaseException.class)
            .hasMessageContaining(ErrorCode.INVALID_FILE_PATH.getDefaultMessage());
    }

    @Test
    void 파일이_존재하면_정상적으로_검증된다() throws IOException {
        // given
        File existingFile = new File(tempDir.toFile(), "existing_file.txt");
        existingFile.createNewFile();

        // when
        FileUtils.validateFileExists(existingFile.toPath());

        // then
        assertThat(existingFile).exists();
    }

    @Test
    void 파일이_존재하지_않으면_에러가_발생한다() {
        // given
        Path invalidPath = tempDir.resolve("nonexistent_file.txt");

        // when, then
        assertThatThrownBy(() -> FileUtils.validateFileExists(invalidPath))
            .isInstanceOf(BaseException.class)
            .hasMessageContaining(ErrorCode.INVALID_FILE_PATH.getDefaultMessage());
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
    void 파일_존재시_부모_디렉토리_생성이_정상적으로_작동한다() {
        // given
        File existingDirectory = new File(tempDir.toFile(), "existing_directory");
        existingDirectory.mkdirs();

        File fileInExistingDirectory = new File(existingDirectory, "file.txt");

        // when
        FileUtils.ensureParentDirectoryExists(fileInExistingDirectory, tempDir.toString());

        // then
        assertThat(existingDirectory).exists();
    }

    @Test
    void 이미_존재하는_디렉토리에_대해_예외가_발생하지_않는다() {
        // given
        File existingDirectory = new File(tempDir.toFile(), "existing_directory");
        existingDirectory.mkdirs(); // 디렉토리 미리 생성

        File fileInExistingDirectory = new File(existingDirectory, "file.txt");

        // when, then
        assertThatCode(() -> FileUtils.ensureParentDirectoryExists(fileInExistingDirectory, tempDir.toString()))
            .doesNotThrowAnyException();
    }

    @Test
    void 디렉토리가_존재하지_않으면_정상적으로_생성된다() {
        // given
        File newDirectory = new File(tempDir.toFile(), "new_directory");

        // when
        FileUtils.ensureParentDirectoryExists(newDirectory, tempDir.toString());

        // then
        assertThat(newDirectory.getParentFile()).exists();
    }

    @Test
    void 부모_디렉토리_생성에_실패하면_예외가_발생한다() {
        // given
        String invalidDirectoryPath = "/root/invalid_directory";
        File invalidDir = new File(invalidDirectoryPath);

        // when, then
        assertThatThrownBy(() -> FileUtils.ensureParentDirectoryExists(invalidDir, tempDir.toString()))
            .isInstanceOf(BaseException.class)
            .hasMessageContaining(ErrorCode.INVALID_FILE_PATH.getDefaultMessage());
    }

    @Test
    void 잘못된_경로로_디렉토리_생성에_실패한다() {
        // given
        String invalidDirectoryPath = "../../invalid_directory";
        File invalidDir = new File(tempDir.toFile(), invalidDirectoryPath);

        // when, then
        assertThatThrownBy(() -> FileUtils.ensureParentDirectoryExists(invalidDir, tempDir.toString()))
            .isInstanceOf(BaseException.class)
            .hasMessageContaining(ErrorCode.INVALID_FILE_PATH.getDefaultMessage());
    }

    @Test
    void 파일_확장자와_속성이_유효하면_정상적으로_검증된다() {
        // given
        String fileName = "valid_file.txt";
        Set<String> disallowedExtensions = Set.of("exe", "bat");

        // when, then
        assertThatCode(() -> FileUtils.validateFileAttributes(fileName, disallowedExtensions))
            .doesNotThrowAnyException();
    }

    @Test
    void 허용되지_않은_확장자가_있으면_예외가_발생한다() {
        // given
        String fileName = "malicious_file.exe";
        Set<String> disallowedExtensions = Set.of("exe", "bat");

        // when, then
        assertThatThrownBy(() -> FileUtils.validateFileAttributes(fileName, disallowedExtensions))
            .isInstanceOf(BaseException.class)
            .hasMessageContaining(ErrorCode.INVALID_FILE_ATTRIBUTE.getDefaultMessage());
    }

    @Test
    void 잘못된_파일명이_검증되면_예외가_발생한다() {
        // given
        String invalidFileName = "../invalid.txt";

        // when, then
        assertThatThrownBy(() -> FileUtils.validateFilename(invalidFileName))
            .isInstanceOf(BaseException.class)
            .hasMessageContaining(ErrorCode.INVALID_FILE_NAME.getDefaultMessage());
    }

    @Test
    void 유효한_파일명이면_예외가_발생하지_않는다() {
        // given
        String validFileName = "valid.txt";

        // when, then
        assertThatCode(() -> FileUtils.validateFilename(validFileName))
            .doesNotThrowAnyException();
    }

    @Test
    void 파일명이_null이면_예외가_발생하지_않는다() {
        // when, then
        assertThatCode(() -> FileUtils.validateFilename(null))
            .doesNotThrowAnyException();
    }

    @Test
    void 파일명이_빈_문자열이면_예외가_발생하지_않는다() {
        // when, then
        assertThatCode(() -> FileUtils.validateFilename(" "))
            .doesNotThrowAnyException();
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
        assertThat(readOnlyFile).canRead();
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
            .isInstanceOf(BaseException.class)
            .hasMessageContaining(ErrorCode.FILE_PERMISSION_ERROR.getDefaultMessage());
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
