package com.stempo.service;

import com.stempo.model.UploadedFile;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UploadedFileService {

    UploadedFile saveUploadedFile(UploadedFile uploadedFile);

    Page<UploadedFile> getUploadedFiles(Pageable pageable);

    Optional<UploadedFile> getUploadedFileByOriginalFileName(String fileName);

    UploadedFile getUploadedFileByUrl(String url);

    void deleteUploadedFile(UploadedFile uploadedFile);

    void verifyFilesExist(List<String> fileUrls);
}
