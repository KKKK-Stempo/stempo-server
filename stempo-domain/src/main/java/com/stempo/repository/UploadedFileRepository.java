package com.stempo.repository;


import com.stempo.model.UploadedFile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface UploadedFileRepository {

    UploadedFile save(UploadedFile uploadedFile);

    Page<UploadedFile> findAll(Pageable pageable);

    Optional<UploadedFile> findByOriginalFileName(String outputFilename);

    UploadedFile findByUrlOrThrow(String url);

    void delete(UploadedFile uploadedFile);

    long countByUrlIn(List<String> fileUrls);
}
