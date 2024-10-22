package com.stempo.util;

import com.stempo.exception.DirectoryCreationException;
import com.stempo.exception.FilePermissionException;
import com.stempo.exception.InvalidFileAttributeException;
import com.stempo.exception.InvalidFileNameException;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.PosixFilePermission;
import java.util.EnumSet;
import java.util.Set;
import java.util.UUID;
import org.apache.commons.io.FilenameUtils;

public class FileUtils {

    /**
     * 주어진 파일 경로가 기본 디렉토리 내에 포함되는지 확인하고, 정상적인 경로를 반환합니다.
     *
     * @param filePath      검증할 파일 경로
     * @param baseDirectory 기본 디렉토리
     * @return 정상적인 파일 경로일 경우 Path 객체를 반환
     * @throws InvalidPathException 잘못된 경로일 경우 발생
     */
    public static Path validateFilePath(String filePath, String baseDirectory) throws InvalidPathException {
        Path baseDir = Paths.get(baseDirectory).normalize().toAbsolutePath();
        Path resolvedPath = baseDir.resolve(filePath).normalize();

        // 경로가 기본 디렉토리 내부에 있는지 확인
        if (!resolvedPath.startsWith(baseDir)) {
            throw new InvalidPathException(filePath, "Invalid file path: Path traversal detected.");
        }
        return resolvedPath;
    }

    /**
     * 파일이 존재하는지 확인합니다. 파일이 존재하지 않으면 예외를 발생시킵니다.
     *
     * @param filePath 검증할 파일 경로
     * @throws InvalidPathException 파일이 존재하지 않는 경우 발생
     */
    public static void validateFileExists(Path filePath) throws InvalidPathException {
        if (!filePath.toFile().exists()) {
            throw new InvalidPathException(filePath.toString(), "File does not exist: " + filePath);
        }
    }

    /**
     * 고유한 파일명을 생성합니다.
     *
     * @param extension 파일 확장자
     * @return 생성된 고유 파일명
     */
    public static String makeFileName(String extension) {
        return System.nanoTime() + "_" + UUID.randomUUID() + "." + extension;
    }

    /**
     * 디렉토리가 존재하지 않으면 생성합니다.
     *
     * @param file 디렉토리가 포함된 파일 객체
     */
    public static void ensureParentDirectoryExists(File file, String baseDirectory) {
        Path safePath = FileUtils.validateFilePath(file.getPath(), baseDirectory);

        File parentDir = safePath.getParent().toFile();
        if (!parentDir.exists()) {
            if (!parentDir.mkdirs()) {
                throw new DirectoryCreationException("Failed to create directory: " + parentDir.getAbsolutePath());
            }
        }
    }

    /**
     * 파일명과 확장자를 검증합니다.
     *
     * @param originalFilename   파일명
     * @param disallowExtensions 허용되지 않은 확장자 목록
     * @throws IllegalArgumentException 유효하지 않은 파일명이나 확장자일 경우 발생
     */
    public static void validateFileAttributes(String originalFilename, Set<String> disallowExtensions)
            throws IllegalArgumentException {
        String extension = FilenameUtils.getExtension(originalFilename);
        validateFilename(originalFilename);
        if (!validateExtension(extension, disallowExtensions)) {
            throw new InvalidFileAttributeException("Invalid file extension: " + extension);
        }
    }

    /**
     * 파일명이 유효한지 확인하고, 유효하지 않은 경우 예외를 발생시킵니다.
     * <p>
     * 유효하지 않은 파일명에는 다음과 같은 경우가 포함됩니다:
     * <ul>
     *   <li>파일명이 null이거나 빈 문자열인 경우</li>
     *   <li>파일명에 "..", "/", "\\" 문자가 포함된 경우</li>
     * </ul>
     *
     * @param fileName 검증할 파일명
     * @throws InvalidFileNameException 유효하지 않은 파일명일 경우 발생
     */
    protected static void validateFilename(String fileName) {
        if (fileName == null || fileName.trim().isEmpty()) {
            return;
        }
        if (fileName.contains("..") || fileName.contains("/") || fileName.contains("\\")) {
            throw new InvalidFileNameException("Invalid file name: " + fileName);
        }
    }

    /**
     * 확장자가 허용된 것인지 확인합니다.
     *
     * @param extension          파일 확장자
     * @param disallowExtensions 허용되지 않은 확장자 목록
     * @return 허용된 확장자일 경우 true, 그렇지 않을 경우 false
     */
    protected static boolean validateExtension(String extension, Set<String> disallowExtensions) {
        return !disallowExtensions.contains(extension.toLowerCase());
    }

    /**
     * 파일의 읽기 전용 권한을 설정합니다. OS에 따라 적절한 권한을 설정합니다.
     *
     * @param file     파일 객체
     * @param savePath 파일 경로
     * @throws FilePermissionException 파일 권한 설정에 실패한 경우 발생
     */
    public static void setFilePermissions(File file, String savePath, String baseDirectory) {
        try {
            validateFilename(file.getName());
            validateFilePath(file.getPath(), baseDirectory);
            String os = System.getProperty("os.name").toLowerCase();
            if (os.contains("win")) {
                setReadOnlyPermissionsWindows(file, savePath);
            } else {
                setReadOnlyPermissionsUnix(savePath);
            }
        } catch (Exception e) {
            throw new FilePermissionException(
                    "Failed to set file permissions: " + LogSanitizerUtils.sanitizeForLog(savePath));
        }
    }

    /**
     * 윈도우 시스템에서 파일을 읽기 전용으로 설정합니다.
     *
     * @param file     파일 객체
     * @param savePath 파일 경로
     * @throws FilePermissionException 파일 권한 설정에 실패한 경우 발생
     */
    private static void setReadOnlyPermissionsWindows(File file, String savePath) {
        if (!file.setReadOnly()) {
            throw new FilePermissionException(
                    "Failed to set file read-only: " + LogSanitizerUtils.sanitizeForLog(savePath));
        }
    }

    /**
     * POSIX 파일 시스템에서 파일 소유자만 읽을 수 있도록 파일 권한을 설정합니다.
     *
     * @param filePath 파일 경로
     * @throws IOException 권한 설정 실패 시 발생
     */
    private static void setReadOnlyPermissionsUnix(String filePath) throws IOException {
        Path path = Paths.get(filePath);

        // POSIX 파일 권한을 소유자에게만 읽기 권한을 부여하도록 설정
        Set<PosixFilePermission> permissions = EnumSet.of(
                PosixFilePermission.OWNER_READ
        );
        Files.setPosixFilePermissions(path, permissions);
    }

    /**
     * 주어진 파일 크기를 적절한 단위로 포맷팅합니다.
     * <p>
     * 파일 크기는 바이트 단위로 제공되며, KB, MB, GB 단위로 포맷팅됩니다.
     *
     * <ul>
     *     <li>1 KB 미만: "B" 단위 (바이트)</li>
     *     <li>1 MB 미만: "KB" 단위 (킬로바이트), 소수점 둘째 자리까지 표시</li>
     *     <li>1 GB 미만: "MB" 단위 (메가바이트), 소수점 둘째 자리까지 표시</li>
     *     <li>1 GB 이상: "GB" 단위 (기가바이트), 소수점 둘째 자리까지 표시</li>
     * </ul>
     *
     * @param fileSize 파일의 크기(바이트 단위)
     * @return 포맷팅된 파일 크기 문자열 (예: "10.00KB", "1.50MB", "3.00GB")
     */
    public static String formatFileSize(long fileSize) {
        final long KB = 1024;
        final long MB = KB * 1024;
        final long GB = MB * 1024;

        if (fileSize < KB) {
            return fileSize + " B";
        } else if (fileSize < MB) {
            return String.format("%.2fKB", fileSize / (double) KB);
        } else if (fileSize < GB) {
            return String.format("%.2fMB", fileSize / (double) MB);
        } else {
            return String.format("%.2fGB", fileSize / (double) GB);
        }
    }
}
