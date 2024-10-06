package com.stempo.api.domain.domain.repository;


import com.stempo.api.domain.domain.model.UploadedFile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface UploadedFileRepository {

    UploadedFile save(UploadedFile uploadedFile);

    Page<UploadedFile> findAll(Pageable pageable);

    Optional<UploadedFile> findByOriginalFileName(String outputFilename);
}
