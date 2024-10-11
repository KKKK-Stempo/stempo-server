package com.stempo.api.domain.persistence.repository;


import com.stempo.api.domain.persistence.entity.UploadedFileEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UploadedFileJpaRepository extends JpaRepository<UploadedFileEntity, Long> {

    Optional<UploadedFileEntity> findByOriginalFileName(String fileName);

    Optional<UploadedFileEntity> findByUrl(String url);

    long countByUrlIn(List<String> fileUrls);
}
