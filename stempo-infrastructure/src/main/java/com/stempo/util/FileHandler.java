package com.stempo.util;

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
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@Component
@Configuration
@Slf4j
public class FileHandler {

    private String filePath;
    private final Set<String> disallowExtensions = new HashSet<>();

    public FileHandler(
            @Value("${resource.file.disallow-extension}") String[] disallowExtensions,
            @Value("${resource.file.path}") String filePath
    ) {
        this.filePath = filePath;
        this.disallowExtensions.addAll(Arrays.asList(disallowExtensions));
    }

    public void init() {
        filePath = filePath.replace("/", File.separator).replace("\\", File.separator);
    }

    public String saveFile(MultipartFile multipartFile, String category) throws IOException {
        init();
        String originalFilename = multipartFile.getOriginalFilename();
        String extension = FilenameUtils.getExtension(originalFilename);
        FileUtils.validateFileAttributes(originalFilename, disallowExtensions);

        String saveFilename = FileUtils.makeFileName(extension);
        String savePath = filePath + File.separator + category + File.separator + saveFilename;

        File file = new File(savePath);
        FileUtils.ensureParentDirectoryExists(file, filePath);
        multipartFile.transferTo(file);
        FileUtils.setFilePermissions(file, savePath, filePath);
        return savePath;
    }

    public String saveFile(File file) throws IOException {
        String originalFilename = file.getName();
        String extension = FilenameUtils.getExtension(originalFilename);
        FileUtils.validateFileAttributes(originalFilename, disallowExtensions);

        String saveFilename = FileUtils.makeFileName(extension);
        String savePath = filePath + File.separator + saveFilename;

        File destination = new File(savePath);
        FileUtils.ensureParentDirectoryExists(destination, filePath);

        Files.copy(file.toPath(), destination.toPath(), StandardCopyOption.REPLACE_EXISTING);
        FileUtils.setFilePermissions(destination, savePath, filePath);
        return savePath;
    }

    public boolean deleteFile(String savedPath) {
        File fileToDelete = new File(savedPath);
        boolean deleted = fileToDelete.delete();
        if (!deleted) {
            log.error("Failed to delete file: {}", LogSanitizerUtils.sanitizeForLog(savedPath));
        }
        return deleted;
    }
}
