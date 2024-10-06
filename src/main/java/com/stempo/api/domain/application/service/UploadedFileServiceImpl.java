package com.stempo.api.domain.application.service;

import com.stempo.api.domain.domain.model.UploadedFile;
import com.stempo.api.domain.domain.repository.UploadedFileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UploadedFileServiceImpl implements UploadedFileService {

    private final UploadedFileRepository uploadedFileRepository;

    @Override
    @Transactional
    public UploadedFile saveUploadedFile(UploadedFile uploadedFile) {
        return uploadedFileRepository.save(uploadedFile);
    }

    @Override
    public Page<UploadedFile> getUploadedFiles(Pageable pageable) {
        return uploadedFileRepository.findAll(pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<UploadedFile> getUploadedFileByOriginalFileName(String fileName) {
        return uploadedFileRepository.findByOriginalFileName(fileName);
    }
}
