package com.stempo.controller;

import com.stempo.annotation.SuccessApiResponse;
import com.stempo.dto.ApiResponse;
import com.stempo.dto.PagedResponseDto;
import com.stempo.dto.request.DeleteFileRequestDto;
import com.stempo.dto.response.UploadedFileResponseDto;
import com.stempo.exception.InvalidColumnException;
import com.stempo.exception.SortingArgumentException;
import com.stempo.service.FileService;
import com.stempo.util.PageableUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
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
    @SuccessApiResponse(data = "[\"filePath1\", \"filePath2\"]", dataType = List.class, dataDescription = "파일 경로 리스트")
    @PreAuthorize("hasRole('USER')")
    @PostMapping(value = "/api/v1/files/boards", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ApiResponse<List<String>> boardFileUpload(
            @RequestParam(name = "multipartFile", required = false) List<MultipartFile> multipartFiles
    ) throws IOException {
        List<String> filePaths = fileService.saveFiles(multipartFiles, "boards");
        return ApiResponse.success(filePaths);
    }

    @Operation(summary = "[A] 파일 목록 조회", description = "ROLE_ADMIN 이상의 권한이 필요함<br>" +
            "DTO의 필드명을 기준으로 정렬 가능하며, 정렬 방향은 오름차순(asc)과 내림차순(desc)이 가능함")
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/api/v1/files")
    public ApiResponse<PagedResponseDto<UploadedFileResponseDto>> getFiles(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "20") int size,
            @RequestParam(name = "sortBy", defaultValue = "createdAt") List<String> sortBy,
            @RequestParam(name = "sortDirection", defaultValue = "desc") List<String> sortDirection
    ) throws InvalidColumnException, SortingArgumentException {
        Pageable pageable = PageableUtils.createPageable(page, size, sortBy, sortDirection, UploadedFileResponseDto.class);
        PagedResponseDto<UploadedFileResponseDto> uploadedFiles = fileService.getFiles(pageable);
        return ApiResponse.success(uploadedFiles);
    }

    @Operation(summary = "[A] 파일 삭제", description = "ROLE_ADMIN 이상의 권한이 필요함<br>" +
            "파일 경로(/resources/files/)를 받아 해당 파일을 삭제함")
    @SuccessApiResponse(data = "true", dataType = Boolean.class, dataDescription = "파일 삭제 여부")
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/api/v1/files")
    public ApiResponse<Boolean> deleteFile(
            @Valid @RequestBody DeleteFileRequestDto requestDto
    ) {
        boolean deleted = fileService.deleteFile(requestDto);
        return ApiResponse.success(deleted);
    }
}
