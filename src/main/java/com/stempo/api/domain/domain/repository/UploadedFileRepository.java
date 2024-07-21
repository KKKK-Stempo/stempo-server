package com.stempo.api.domain.domain.repository;


import com.stempo.api.domain.domain.model.UploadedFile;

import java.util.Optional;

public interface UploadedFileRepository {

    UploadedFile save(UploadedFile uploadedFile);

    Optional<UploadedFile> findByOriginalFileName(String outputFilename);
}
