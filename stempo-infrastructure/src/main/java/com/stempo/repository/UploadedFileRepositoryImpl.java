package com.stempo.repository;

import com.stempo.entity.UploadedFileEntity;
import com.stempo.exception.NotFoundException;
import com.stempo.mapper.UploadedFileMapper;
import com.stempo.model.UploadedFile;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

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
    public Page<UploadedFile> findAll(Pageable pageable) {
        return repository.findAll(pageable)
                .map(mapper::toDomain);
    }

    @Override
    public Optional<UploadedFile> findByOriginalFileName(String fileName) {
        return repository.findByOriginalFileName(fileName)
                .map(mapper::toDomain);
    }

    @Override
    public UploadedFile findByUrlOrThrow(String url) {
        return repository.findByUrl(url)
                .map(mapper::toDomain)
                .orElseThrow(() -> new NotFoundException("[UploadedFile] url: " + url + " not found"));
    }

    @Override
    public void delete(UploadedFile uploadedFile) {
        UploadedFileEntity jpaEntity = mapper.toEntity(uploadedFile);
        repository.delete(jpaEntity);
    }

    @Override
    public long countByUrlIn(List<String> fileUrls) {
        return repository.countByUrlIn(fileUrls);
    }
}
