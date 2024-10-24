package com.stempo.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.stempo.exception.ResourceNotFoundException;
import com.stempo.model.UploadedFile;
import com.stempo.repository.UploadedFileRepository;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
class UploadedFileServiceImplTest {

    @Mock
    private UploadedFileRepository uploadedFileRepository;

    @InjectMocks
    private UploadedFileServiceImpl uploadedFileService;

    private UploadedFile uploadedFile;

    @BeforeEach
    void setUp() {
        uploadedFile = UploadedFile.builder()
                .id(1L)
                .url("test-url")
                .originalFileName("test-file.txt")
                .build();
    }

    @Test
    void 파일을_저장하면_저장된_파일을_반환한다() {
        // given
        when(uploadedFileRepository.save(any(UploadedFile.class))).thenReturn(uploadedFile);

        // when
        UploadedFile result = uploadedFileService.saveUploadedFile(uploadedFile);

        // then
        assertThat(result).isEqualTo(uploadedFile);
        verify(uploadedFileRepository).save(uploadedFile);
    }

    @Test
    void 페이징된_파일_목록을_반환한다() {
        // given
        Pageable pageable = PageRequest.of(0, 10);
        Page<UploadedFile> page = new PageImpl<>(List.of(uploadedFile), pageable, 1);

        when(uploadedFileRepository.findAll(pageable)).thenReturn(page);

        // when
        Page<UploadedFile> result = uploadedFileService.getUploadedFiles(pageable);

        // then
        assertThat(result).isEqualTo(page);
        verify(uploadedFileRepository).findAll(pageable);
    }

    @Test
    void 원본_파일명으로_파일을_조회하면_파일을_반환한다() {
        // given
        String originalFileName = "test-file.txt";
        when(uploadedFileRepository.findByOriginalFileName(originalFileName))
                .thenReturn(Optional.of(uploadedFile));

        // when
        Optional<UploadedFile> result = uploadedFileService.getUploadedFileByOriginalFileName(originalFileName);

        // then
        assertThat(result).isPresent().contains(uploadedFile);
        verify(uploadedFileRepository).findByOriginalFileName(originalFileName);
    }

    @Test
    void URL로_파일을_조회하면_파일을_반환한다() {
        // given
        String url = "test-url";
        when(uploadedFileRepository.findByUrlOrThrow(url)).thenReturn(uploadedFile);

        // when
        UploadedFile result = uploadedFileService.getUploadedFileByUrl(url);

        // then
        assertThat(result).isEqualTo(uploadedFile);
        verify(uploadedFileRepository).findByUrlOrThrow(url);
    }

    @Test
    void 파일을_삭제한다() {
        // when
        uploadedFileService.deleteUploadedFile(uploadedFile);

        // then
        verify(uploadedFileRepository).delete(uploadedFile);
    }

    @Test
    void 파일이_존재하면_검증을_통과한다() {
        // given
        List<String> fileUrls = List.of("url1", "url2");
        when(uploadedFileRepository.countByUrlIn(fileUrls)).thenReturn(2L);

        // when
        uploadedFileService.verifyFilesExist(fileUrls);

        // then
        verify(uploadedFileRepository).countByUrlIn(fileUrls);
    }

    @Test
    void 파일이_존재하지_않으면_예외를_던진다() {
        // given
        List<String> fileUrls = List.of("url1", "url2");
        when(uploadedFileRepository.countByUrlIn(fileUrls)).thenReturn(1L);

        // when, then
        assertThatThrownBy(() -> uploadedFileService.verifyFilesExist(fileUrls))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("One or more files do not exist on the server.");
        verify(uploadedFileRepository).countByUrlIn(fileUrls);
    }

    @Test
    void 파일_목록이_null이거나_빈_경우_검증을_통과한다() {
        // when
        uploadedFileService.verifyFilesExist(null);
        uploadedFileService.verifyFilesExist(List.of());

        // then
        verifyNoInteractions(uploadedFileRepository);
    }
}
