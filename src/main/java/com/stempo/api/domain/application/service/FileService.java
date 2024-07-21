package com.stempo.api.domain.application.service;


import com.stempo.api.domain.application.handler.FileHandler;
import com.stempo.api.domain.domain.model.UploadedFile;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

@Service
@RequiredArgsConstructor
public class FileService {

    private final FileHandler fileHandler;
    private final UploadedFileService uploadedFileService;

    @Value("${resource.file.url}")
    private String fileURL;

    public String saveFile(MultipartFile multipartFile) throws IOException {
        String savedFilePath = fileHandler.saveFile(multipartFile);
        String fileName = new File(savedFilePath).getName();
        String url = fileURL + "/" + fileName;

        UploadedFile uploadedFile = UploadedFile.create(multipartFile.getOriginalFilename(), fileName, savedFilePath, url, multipartFile.getSize(), multipartFile.getContentType());
        uploadedFileService.saveUploadedFile(uploadedFile);
        return savedFilePath;
    }
}

