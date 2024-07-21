package com.stempo.api.domain.application.service;

import com.stempo.api.domain.domain.model.UploadedFile;

import java.util.Optional;

public interface UploadedFileService {

    UploadedFile saveUploadedFile(UploadedFile uploadedFile);

    Optional<UploadedFile> getUploadedFileByOriginalFileName(String fileName);
}
