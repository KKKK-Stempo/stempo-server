package com.stempo.api.domain.presentation;

import com.stempo.api.domain.application.service.FileService;
import com.stempo.api.global.common.dto.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Tag(name = "File Upload", description = "파일 업로드")
public class FileController {

    private final FileService fileService;

    @Operation(summary = "[U] 게시판 파일 업로드", description = "ROLE_USER 이상의 권한이 필요함")
    @PostMapping(value = "/api/v1/files/boards", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ApiResponse<List<String>> boardFileUpload(
            @RequestParam(name = "multipartFile", required = false) List<MultipartFile> multipartFiles
    ) throws IOException {
        List<String> filePaths = fileService.saveFiles(multipartFiles, "boards");
        return ApiResponse.success(filePaths);
    }
}
