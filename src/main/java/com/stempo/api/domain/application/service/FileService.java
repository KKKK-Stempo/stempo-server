package com.stempo.api.domain.application.service;


import com.stempo.api.domain.application.handler.FileHandler;
import com.stempo.api.domain.domain.model.UploadedFile;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FileService {

    private final UploadedFileService uploadedFileService;
    private final FileHandler fileHandler;

    @Value("${resource.file.url}")
    private String fileURL;

    public List<String> saveFiles(List<MultipartFile> multipartFiles, String path) throws IOException {
        List<String> filePaths = new ArrayList<>();
        for (MultipartFile multipartFile : multipartFiles) {
            String filePath = saveFile(multipartFile, path);
            filePaths.add(filePath);
        }
        return filePaths;
    }

    public String saveFile(MultipartFile multipartFile, String path) throws IOException {
        String savedFilePath = fileHandler.saveFile(multipartFile, path);
        String fileName = new File(savedFilePath).getName();
        String url = fileURL + "/" + path.replace(File.separator, "/") + "/" + fileName;

        UploadedFile uploadedFile = UploadedFile.create(multipartFile.getOriginalFilename(), fileName, savedFilePath, url, multipartFile.getSize());
        uploadedFileService.saveUploadedFile(uploadedFile);
        return uploadedFile.getUrl();
    }

    public String saveFile(File file) throws IOException {
        String savedFilePath = fileHandler.saveFile(file);
        String fileName = new File(savedFilePath).getName();
        String url = fileURL + "/" + fileName;

        UploadedFile uploadedFile = UploadedFile.create(file.getName(), fileName, savedFilePath, url, file.length());
        uploadedFileService.saveUploadedFile(uploadedFile);
        return url;
    }
}

