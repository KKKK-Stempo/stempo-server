package com.stempo.api.domain.application.service;


import com.stempo.api.domain.application.handler.FileHandler;
import com.stempo.api.domain.domain.model.UploadedFile;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;

@Service
@RequiredArgsConstructor
public class FileService {

    private final FileHandler fileHandler;
    private final UploadedFileService uploadedFileService;

    @Value("${resource.file.url}")
    private String fileURL;

    public String saveFile(File file) throws IOException {
        String savedFilePath = fileHandler.saveFile(file);
        String fileName = new File(savedFilePath).getName();
        String url = fileURL + "/" + fileName;

        UploadedFile uploadedFile = UploadedFile.create(file.getName(), fileName, savedFilePath, url, file.length(), null);
        uploadedFileService.saveUploadedFile(uploadedFile);
        return url;
    }
}

