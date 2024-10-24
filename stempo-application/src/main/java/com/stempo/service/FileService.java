package com.stempo.service;


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
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class FileService {

    private final UploadedFileService uploadedFileService;
    private final UploadedFileDtoMapper mapper;
    private final FileHandler fileHandler;
    private final EncryptionUtils encryptionUtils;

    @Value("${resource.file.url}")
    private String fileURL;

    @Transactional
    public List<String> saveFiles(List<MultipartFile> multipartFiles, String path) throws IOException {
        List<String> filePaths = new ArrayList<>();
        for (MultipartFile multipartFile : multipartFiles) {
            String filePath = saveFile(multipartFile, path);
            filePaths.add(filePath);
        }
        return filePaths;
    }

    @Transactional(readOnly = true)
    public PagedResponseDto<UploadedFileResponseDto> getFiles(Pageable pageable) {
        Page<UploadedFile> uploadedFiles = uploadedFileService.getUploadedFiles(pageable);
        return new PagedResponseDto<>(uploadedFiles.map(mapper::toDto));
    }

    public String saveFile(MultipartFile multipartFile, String path) throws IOException {
        String savedFilePath = fileHandler.saveFile(multipartFile, path);
        String fileName = new File(savedFilePath).getName();
        String url = generateFileUrl(path, fileName);

        String encryptedFilePath = encryptionUtils.encrypt(savedFilePath);

        UploadedFile uploadedFile = UploadedFile.create(multipartFile.getOriginalFilename(), fileName,
                encryptedFilePath, url, multipartFile.getSize());
        uploadedFileService.saveUploadedFile(uploadedFile);
        return uploadedFile.getUrl();
    }

    public String saveFile(File file) throws IOException {
        String savedFilePath = fileHandler.saveFile(file);
        String fileName = new File(savedFilePath).getName();
        String url = fileURL + "/" + fileName;

        String encryptedFilePath = encryptionUtils.encrypt(savedFilePath);

        UploadedFile uploadedFile = UploadedFile.create(file.getName(), fileName, encryptedFilePath, url,
                file.length());
        uploadedFileService.saveUploadedFile(uploadedFile);
        return url;
    }

    public boolean deleteFile(DeleteFileRequestDto requestDto) {
        String url = requestDto.getUrl();
        UploadedFile uploadedFile = uploadedFileService.getUploadedFileByUrl(url);

        String filePath = encryptionUtils.decrypt(uploadedFile.getSavedPath());
        boolean deleted = fileHandler.deleteFile(filePath);

        if (!deleted) {
            throw new NotFoundException("File does not exist or could not be deleted");
        }

        uploadedFileService.deleteUploadedFile(uploadedFile);
        return true;
    }

    private String generateFileUrl(String path, String fileName) {
        return fileURL + "/" + path.replace(File.separator, "/") + "/" + fileName;
    }
}

