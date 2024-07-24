package com.stempo.api.domain.application.handler;

import com.stempo.api.domain.application.exception.FileUploadFailException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Component
@Configuration
@Slf4j
public class FileHandler {

    @Value("${resource.file.path}")
    private String filePath;

    public void init() {
        filePath = filePath.replace("/", File.separator).replace("\\", File.separator);
    }

    public String saveFile(MultipartFile multipartFile, String category) throws IOException {
        init();
        String originalFilename = multipartFile.getOriginalFilename();
        String extension = FilenameUtils.getExtension(originalFilename);

        String saveFilename = makeFileName(extension);
        String savePath = filePath + File.separator + category + File.separator + saveFilename;

        File file = new File(savePath);
        ensureParentDirectoryExists(file);
        multipartFile.transferTo(file);
        setFilePermissions(file, savePath, extension);
        return savePath;
    }

    public String saveFile(File file) throws IOException {
        String originalFilename = file.getName();
        String extension = FilenameUtils.getExtension(originalFilename);

        String saveFilename = makeFileName(extension);
        String savePath = filePath + File.separator + saveFilename;

        File destination = new File(savePath);
        ensureParentDirectoryExists(destination);

        Files.copy(file.toPath(), destination.toPath(), StandardCopyOption.REPLACE_EXISTING);
        setFilePermissions(destination, savePath, extension);
        return savePath;
    }

    public String makeFileName(String extension) {
        return (System.nanoTime() + "_" + UUID.randomUUID() + "." + extension);
    }

    private void ensureParentDirectoryExists(File file) {
        if (!file.getParentFile().exists()) {
            boolean isCreated = file.getParentFile().mkdirs();
            if (!isCreated) {
                log.error("Failed to create directory: {}", file.getParentFile().getAbsolutePath());
            }
        }
    }

    private void setFilePermissions(File file, String savePath, String extension) throws FileUploadFailException {
        try {
            String os = System.getProperty("os.name").toLowerCase();
            if (os.contains("win")) {
                boolean readOnly = file.setReadOnly();
                if (!readOnly) {
                    log.error("Failed to set file read-only: {}", savePath);
                }
            } else {
                setFilePermissionsUnix(savePath);
            }
        } catch (Exception e) {
            throw new FileUploadFailException("Failed to upload file: " + savePath, e);
        }
    }

    private void setFilePermissionsUnix(String filePath) throws IOException, InterruptedException {
        ProcessBuilder processBuilder = new ProcessBuilder("chmod", "400", filePath);
        Process process = processBuilder.start();
        int exitCode = process.waitFor();
        if (exitCode != 0) {
            log.error("Failed to set file permissions for: {}", filePath);
        }
    }
}
