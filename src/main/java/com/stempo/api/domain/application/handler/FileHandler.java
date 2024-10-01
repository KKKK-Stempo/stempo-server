package com.stempo.api.domain.application.handler;

import com.stempo.api.global.util.FileUtil;
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

        String saveFilename = FileUtil.makeFileName(extension);
        String savePath = filePath + File.separator + category + File.separator + saveFilename;

        File file = new File(savePath);
        FileUtil.ensureParentDirectoryExists(file, filePath);
        multipartFile.transferTo(file);
        FileUtil.setFilePermissions(file, savePath, extension);
        return savePath;
    }

    public String saveFile(File file) throws IOException {
        String originalFilename = file.getName();
        String extension = FilenameUtils.getExtension(originalFilename);

        String saveFilename = FileUtil.makeFileName(extension);
        String savePath = filePath + File.separator + saveFilename;

        File destination = new File(savePath);
        FileUtil.ensureParentDirectoryExists(destination, filePath);

        Files.copy(file.toPath(), destination.toPath(), StandardCopyOption.REPLACE_EXISTING);
        FileUtil.setFilePermissions(destination, savePath, extension);
        return savePath;
    }
}
