package com.stempo.api.domain.application.service;


import com.stempo.api.domain.application.handler.FileHandler;
import com.stempo.api.domain.domain.model.UploadedFile;
import com.stempo.api.domain.presentation.dto.request.DeleteFileRequestDto;
import com.stempo.api.domain.presentation.dto.response.UploadedFileResponseDto;
import com.stempo.api.global.dto.PagedResponseDto;
import com.stempo.api.global.exception.NotFoundException;
import com.stempo.api.global.util.EncryptionUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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
    private final EncryptionUtil encryptionUtil;

    @Value("${resource.file.url}")
    private String fileURL;

    @Transactional
    public List<String> saveFiles(List<MultipartFile> multipartFiles, String path) throws IOException {
        List<String> filePaths = new ArrayList<>();
        for (MultipartFile multipartFile : multipartFiles) {
            String filePath = saveFile(multipartFile, path);
            filePaths.add(filePath);
        }
        return filePaths;
    }

    @Transactional(readOnly = true)
    public PagedResponseDto<UploadedFileResponseDto> getFiles(Pageable pageable) {
        Page<UploadedFile> uploadedFiles = uploadedFileService.getUploadedFiles(pageable);
        return new PagedResponseDto<>(uploadedFiles.map(UploadedFileResponseDto::toDto));
    }

    public String saveFile(MultipartFile multipartFile, String path) throws IOException {
        String savedFilePath = fileHandler.saveFile(multipartFile, path);
        String fileName = new File(savedFilePath).getName();
        String url = generateFileUrl(path, fileName);

        String encryptedFilePath = encryptionUtil.encrypt(savedFilePath);

        UploadedFile uploadedFile = UploadedFile.create(multipartFile.getOriginalFilename(), fileName, encryptedFilePath, url, multipartFile.getSize());
        uploadedFileService.saveUploadedFile(uploadedFile);
        return uploadedFile.getUrl();
    }

    public String saveFile(File file) throws IOException {
        String savedFilePath = fileHandler.saveFile(file);
        String fileName = new File(savedFilePath).getName();
        String url = fileURL + "/" + fileName;

        String encryptedFilePath = encryptionUtil.encrypt(savedFilePath);

        UploadedFile uploadedFile = UploadedFile.create(file.getName(), fileName, encryptedFilePath, url, file.length());
        uploadedFileService.saveUploadedFile(uploadedFile);
        return url;
    }

    public boolean deleteFile(@Valid DeleteFileRequestDto requestDto) {
        String url = requestDto.getUrl();
        UploadedFile uploadedFile = uploadedFileService.getUploadedFileByUrl(url);

        String filePath = encryptionUtil.decrypt(uploadedFile.getSavedPath());
        File storedFile = new File(filePath);

        if (!storedFile.exists()) {
            throw new NotFoundException("File does not exist");
        }

        boolean deleted = fileHandler.deleteFile(filePath);
        if (deleted) {
            uploadedFileService.deleteUploadedFile(uploadedFile);
        }

        return deleted;
    }

    private String generateFileUrl(String path, String fileName) {
        return fileURL + "/" + path.replace(File.separator, "/") + "/" + fileName;
    }
}

