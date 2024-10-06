package com.stempo.api.domain.application.service;

import com.stempo.api.domain.domain.model.UploadedFile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface UploadedFileService {

    UploadedFile saveUploadedFile(UploadedFile uploadedFile);

    Page<UploadedFile> getUploadedFiles(Pageable pageable);

    Optional<UploadedFile> getUploadedFileByOriginalFileName(String fileName);
}
