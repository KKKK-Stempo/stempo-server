package com.stempo.api.domain.persistence.repository;

import com.stempo.api.domain.domain.model.UploadedFile;
import com.stempo.api.domain.domain.repository.UploadedFileRepository;
import com.stempo.api.domain.persistence.entity.UploadedFileEntity;
import com.stempo.api.domain.persistence.mappper.UploadedFileMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class UploadedFileRepositoryImpl implements UploadedFileRepository {

    private final UploadedFileJpaRepository repository;
    private final UploadedFileMapper mapper;

    @Override
    public UploadedFile save(UploadedFile uploadedFile) {
        UploadedFileEntity jpaEntity = mapper.toEntity(uploadedFile);
        UploadedFileEntity savedEntity = repository.save(jpaEntity);
        return mapper.toDomain(savedEntity);
    }

    @Override
    public Optional<UploadedFile> findByOriginalFileName(String fileName) {
        return repository.findByOriginalFileName(fileName)
                .map(mapper::toDomain);
    }
}
