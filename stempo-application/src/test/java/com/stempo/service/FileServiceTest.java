package com.stempo.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.stempo.dto.PagedResponseDto;
import com.stempo.dto.request.DeleteFileRequestDto;
import com.stempo.dto.response.UploadedFileResponseDto;
import com.stempo.exception.NotFoundException;
import com.stempo.mapper.UploadedFileDtoMapper;
import com.stempo.model.UploadedFile;
import com.stempo.util.EncryptionUtils;
import com.stempo.util.FileHandler;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;

@ExtendWith(MockitoExtension.class)
class FileServiceTest {

    @Mock
    private UploadedFileService uploadedFileService;

    @Mock
    private UploadedFileDtoMapper mapper;

    @Mock
    private FileHandler fileHandler;

    @Mock
    private EncryptionUtils encryptionUtils;

    @InjectMocks
    private FileService fileService;

    @Value("${resource.file.url}")
    private String fileURL = "http://example.com/files";

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(fileService, "fileURL", fileURL);
    }

    @Test
    void saveFiles_여러_파일을_저장하고_URL_리스트를_반환한다() throws IOException {
        // given
        String path = "uploads";
        MultipartFile multipartFile1 = mock(MultipartFile.class);
        MultipartFile multipartFile2 = mock(MultipartFile.class);
        List<MultipartFile> multipartFiles = Arrays.asList(multipartFile1, multipartFile2);

        String savedFilePath1 = "/saved/path/file1.txt";
        String savedFilePath2 = "/saved/path/file2.txt";

        String encryptedFilePath1 = "encryptedPath1";
        String encryptedFilePath2 = "encryptedPath2";

        String fileName1 = "file1.txt";
        String fileName2 = "file2.txt";

        String url1 = fileURL + "/" + path + "/" + fileName1;
        String url2 = fileURL + "/" + path + "/" + fileName2;

        when(multipartFile1.getOriginalFilename()).thenReturn("originalFile1.txt");
        when(multipartFile1.getSize()).thenReturn(100L);

        when(multipartFile2.getOriginalFilename()).thenReturn("originalFile2.txt");
        when(multipartFile2.getSize()).thenReturn(200L);

        when(fileHandler.saveFile(multipartFile1, path)).thenReturn(savedFilePath1);
        when(fileHandler.saveFile(multipartFile2, path)).thenReturn(savedFilePath2);

        when(encryptionUtils.encrypt(savedFilePath1)).thenReturn(encryptedFilePath1);
        when(encryptionUtils.encrypt(savedFilePath2)).thenReturn(encryptedFilePath2);

        // when
        List<String> resultUrls = fileService.saveFiles(multipartFiles, path);

        // then
        assertThat(resultUrls).containsExactlyInAnyOrder(url1, url2);
        verify(fileHandler).saveFile(multipartFile1, path);
        verify(fileHandler).saveFile(multipartFile2, path);
        verify(encryptionUtils).encrypt(savedFilePath1);
        verify(encryptionUtils).encrypt(savedFilePath2);
        verify(uploadedFileService, times(2)).saveUploadedFile(any(UploadedFile.class));
    }

    @Test
    void getFiles_파일_목록을_페이징하여_반환한다() {
        // given
        Pageable pageable = PageRequest.of(0, 10);
        UploadedFile uploadedFile = UploadedFile.builder()
                .id(1L)
                .originalFileName("original.txt")
                .saveFileName("file.txt")
                .url("http://example.com/files/file.txt")
                .build();
        UploadedFileResponseDto responseDto = UploadedFileResponseDto.builder()
                .originalFileName(uploadedFile.getOriginalFileName())
                .url(uploadedFile.getUrl())
                .createdAt(uploadedFile.getCreatedAt())
                .build();

        Page<UploadedFile> uploadedFilesPage = new PageImpl<>(Collections.singletonList(uploadedFile), pageable, 1);

        when(uploadedFileService.getUploadedFiles(pageable)).thenReturn(uploadedFilesPage);
        when(mapper.toDto(uploadedFile)).thenReturn(responseDto);

        // when
        PagedResponseDto<UploadedFileResponseDto> result = fileService.getFiles(pageable);

        // then
        assertThat(result.getItems()).containsExactly(responseDto);
        verify(uploadedFileService).getUploadedFiles(pageable);
        verify(mapper).toDto(uploadedFile);
    }

    @Test
    void saveFile_MultipartFile을_저장하고_URL을_반환한다() throws IOException {
        // given
        String path = "uploads";
        MultipartFile multipartFile = mock(MultipartFile.class);

        String savedFilePath = "/saved/path/file.txt";
        String encryptedFilePath = "encryptedPath";
        String fileName = "file.txt";
        String url = fileURL + "/" + path + "/" + fileName;

        when(multipartFile.getOriginalFilename()).thenReturn("originalFile.txt");
        when(multipartFile.getSize()).thenReturn(100L);

        when(fileHandler.saveFile(multipartFile, path)).thenReturn(savedFilePath);
        when(encryptionUtils.encrypt(savedFilePath)).thenReturn(encryptedFilePath);

        // when
        String resultUrl = fileService.saveFile(multipartFile, path);

        // then
        assertThat(resultUrl).isEqualTo(url);
        verify(fileHandler).saveFile(multipartFile, path);
        verify(encryptionUtils).encrypt(savedFilePath);
        verify(uploadedFileService).saveUploadedFile(any(UploadedFile.class));
    }

    @Test
    void saveFile_File을_저장하고_URL을_반환한다() throws IOException {
        // given
        File file = mock(File.class);
        String savedFilePath = "/saved/path/file.txt";
        String encryptedFilePath = "encryptedPath";
        String fileName = "file.txt";
        String url = fileURL + "/" + fileName;

        when(file.getName()).thenReturn("originalFile.txt");
        when(file.length()).thenReturn(100L);

        when(fileHandler.saveFile(file)).thenReturn(savedFilePath);
        when(encryptionUtils.encrypt(savedFilePath)).thenReturn(encryptedFilePath);

        // when
        String resultUrl = fileService.saveFile(file);

        // then
        assertThat(resultUrl).isEqualTo(url);
        verify(fileHandler).saveFile(file);
        verify(encryptionUtils).encrypt(savedFilePath);
        verify(uploadedFileService).saveUploadedFile(any(UploadedFile.class));
    }

    @Test
    void deleteFile_파일을_삭제하고_true를_반환한다() {
        // given
        String url = "http://example.com/files/file.txt";
        String encryptedFilePath = "encryptedPath";
        String decryptedFilePath = "/saved/path/file.txt";

        DeleteFileRequestDto requestDto = new DeleteFileRequestDto();
        requestDto.setUrl(url);

        UploadedFile uploadedFile = UploadedFile.builder()
                .savedPath(encryptedFilePath)
                .url(url)
                .build();

        when(uploadedFileService.getUploadedFileByUrl(url)).thenReturn(uploadedFile);
        when(encryptionUtils.decrypt(encryptedFilePath)).thenReturn(decryptedFilePath);
        when(fileHandler.deleteFile(decryptedFilePath)).thenReturn(true);

        // when
        boolean result = fileService.deleteFile(requestDto);

        // then
        assertThat(result).isTrue();
        verify(uploadedFileService).getUploadedFileByUrl(url);
        verify(encryptionUtils).decrypt(encryptedFilePath);
        verify(fileHandler).deleteFile(decryptedFilePath);
        verify(uploadedFileService).deleteUploadedFile(uploadedFile);
    }

    @Test
    void deleteFile_파일이_존재하지_않거나_삭제에_실패하면_NotFoundException을_던진다() {
        // given
        String url = "http://example.com/files/file.txt";
        String encryptedFilePath = "encryptedPath";
        String decryptedFilePath = "/saved/path/file.txt";

        DeleteFileRequestDto requestDto = new DeleteFileRequestDto();
        requestDto.setUrl(url);

        UploadedFile uploadedFile = UploadedFile.builder()
                .savedPath(encryptedFilePath)
                .url(url)
                .build();

        when(uploadedFileService.getUploadedFileByUrl(url)).thenReturn(uploadedFile);
        when(encryptionUtils.decrypt(encryptedFilePath)).thenReturn(decryptedFilePath);
        when(fileHandler.deleteFile(decryptedFilePath)).thenReturn(false);

        // when, then
        assertThatThrownBy(() -> fileService.deleteFile(requestDto))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("File does not exist or could not be deleted");

        verify(uploadedFileService).getUploadedFileByUrl(url);
        verify(encryptionUtils).decrypt(encryptedFilePath);
        verify(fileHandler).deleteFile(decryptedFilePath);
    }
}
