package com.stempo.api.domain.application.service;

import com.stempo.api.domain.domain.model.UploadedFile;

public interface UploadedFileService {
    UploadedFile saveUploadedFile(UploadedFile uploadedFile);
}
