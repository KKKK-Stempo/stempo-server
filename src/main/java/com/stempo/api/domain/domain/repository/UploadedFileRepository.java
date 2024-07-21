package com.stempo.api.domain.domain.repository;


import com.stempo.api.domain.domain.model.UploadedFile;

public interface UploadedFileRepository {
    UploadedFile save(UploadedFile uploadedFile);
}
