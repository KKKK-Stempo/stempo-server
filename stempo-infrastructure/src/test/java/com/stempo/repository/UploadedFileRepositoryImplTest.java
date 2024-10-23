package com.stempo.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.stempo.entity.UploadedFileEntity;
import com.stempo.exception.NotFoundException;
import com.stempo.mapper.UploadedFileMapper;
import com.stempo.model.UploadedFile;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

class UploadedFileRepositoryImplTest {

    @Mock
    private UploadedFileJpaRepository uploadedFileJpaRepository;

    @Mock
    private UploadedFileMapper uploadedFileMapper;

    @InjectMocks
    private UploadedFileRepositoryImpl uploadedFileRepository;

    private UploadedFile uploadedFile;
    private UploadedFileEntity uploadedFileEntity;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        uploadedFile = UploadedFile.builder()
                .id(1L)
                .originalFileName("testFile.txt")
                .saveFileName("savedTestFile.txt")
                .savedPath("/uploads/files")
                .url("http://localhost/uploads/files/savedTestFile.txt")
                .fileSize(1024L)
                .createdAt(LocalDateTime.now())
                .build();

        uploadedFileEntity = UploadedFileEntity.builder()
                .id(1L)
                .originalFileName("testFile.txt")
                .saveFileName("savedTestFile.txt")
                .savedPath("/uploads/files")
                .url("http://localhost/uploads/files/savedTestFile.txt")
                .fileSize(1024L)
                .build();
        uploadedFileEntity.setCreatedAt(LocalDateTime.now());
    }

    @Test
    void 파일을_저장하면_성공한다() {
        // given
        when(uploadedFileMapper.toEntity(any(UploadedFile.class))).thenReturn(uploadedFileEntity);
        when(uploadedFileJpaRepository.save(any(UploadedFileEntity.class))).thenReturn(uploadedFileEntity);
        when(uploadedFileMapper.toDomain(any(UploadedFileEntity.class))).thenReturn(uploadedFile);

        // when
        UploadedFile savedFile = uploadedFileRepository.save(uploadedFile);

        // then
        assertThat(savedFile).isNotNull();
        assertThat(savedFile.getId()).isEqualTo(uploadedFile.getId());
        verify(uploadedFileJpaRepository, times(1)).save(uploadedFileEntity);
    }

    @Test
    void 모든_파일을_페이지네이션으로_조회하면_성공한다() {
        // given
        Pageable pageable = PageRequest.of(0, 10);
        Page<UploadedFileEntity> entities = new PageImpl<>(List.of(uploadedFileEntity));
        when(uploadedFileJpaRepository.findAll(pageable)).thenReturn(entities);
        when(uploadedFileMapper.toDomain(any(UploadedFileEntity.class))).thenReturn(uploadedFile);

        // when
        Page<UploadedFile> result = uploadedFileRepository.findAll(pageable);

        // then
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().getFirst().getOriginalFileName()).isEqualTo("testFile.txt");
    }

    @Test
    void 파일명을_이용해_파일을_조회하면_성공한다() {
        // given
        when(uploadedFileJpaRepository.findByOriginalFileName("testFile.txt")).thenReturn(
                Optional.of(uploadedFileEntity));
        when(uploadedFileMapper.toDomain(any(UploadedFileEntity.class))).thenReturn(uploadedFile);

        // when
        Optional<UploadedFile> result = uploadedFileRepository.findByOriginalFileName("testFile.txt");

        // then
        assertThat(result).isPresent();
        assertThat(result.get().getOriginalFileName()).isEqualTo("testFile.txt");
    }

    @Test
    void 파일의_URL로_조회하면_성공한다() {
        // given
        when(uploadedFileJpaRepository.findByUrl("http://localhost/uploads/files/savedTestFile.txt")).thenReturn(
                Optional.of(uploadedFileEntity));
        when(uploadedFileMapper.toDomain(any(UploadedFileEntity.class))).thenReturn(uploadedFile);

        // when
        UploadedFile result = uploadedFileRepository.findByUrlOrThrow(
                "http://localhost/uploads/files/savedTestFile.txt");

        // then
        assertThat(result).isNotNull();
        assertThat(result.getUrl()).isEqualTo("http://localhost/uploads/files/savedTestFile.txt");
    }

    @Test
    void 파일의_URL로_조회시_존재하지_않으면_예외가_발생한다() {
        // given
        when(uploadedFileJpaRepository.findByUrl("http://localhost/uploads/files/savedTestFile.txt")).thenReturn(
                Optional.empty());

        // when, then
        assertThrows(NotFoundException.class,
                () -> uploadedFileRepository.findByUrlOrThrow("http://localhost/uploads/files/savedTestFile.txt"));
    }

    @Test
    void 파일을_삭제하면_성공한다() {
        // given
        when(uploadedFileMapper.toEntity(any(UploadedFile.class))).thenReturn(uploadedFileEntity);

        // when
        uploadedFileRepository.delete(uploadedFile);

        // then
        verify(uploadedFileJpaRepository, times(1)).delete(uploadedFileEntity);
    }

    @Test
    void 여러_URL로_파일_갯수를_조회하면_성공한다() {
        // given
        List<String> fileUrls = List.of("http://localhost/uploads/files/savedTestFile.txt");
        when(uploadedFileJpaRepository.countByUrlIn(fileUrls)).thenReturn(1L);

        // when
        long count = uploadedFileRepository.countByUrlIn(fileUrls);

        // then
        assertThat(count).isEqualTo(1L);
    }
}
