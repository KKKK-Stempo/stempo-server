package com.stempo.api.domain.persistence.repository;


import com.stempo.api.domain.persistence.entity.UploadedFileEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UploadedFileJpaRepository extends JpaRepository<UploadedFileEntity, Long> {
    Optional<UploadedFileEntity> findByOriginalFileName(String fileName);
}
