package com.stempo.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.stempo.dto.PagedResponseDto;
import com.stempo.dto.request.DeleteFileRequestDto;
import com.stempo.dto.response.UploadedFileResponseDto;
import com.stempo.exception.BaseException;
import com.stempo.exception.ErrorCode;
import com.stempo.service.FileService;
import com.stempo.test.TestApplication;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = FileController.class)
@ContextConfiguration(classes = TestApplication.class)
@ActiveProfiles("test")
class FileControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private FileService fileService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithMockUser(roles = "USER")
    void 정상적으로_게시판_파일을_업로드한다() throws Exception {
        // given
        MockMultipartFile file1 = new MockMultipartFile(
            "multipartFile",
            "file1.txt",
            MediaType.TEXT_PLAIN_VALUE,
            "Hello, World!".getBytes(StandardCharsets.UTF_8)
        );

        MockMultipartFile file2 = new MockMultipartFile(
            "multipartFile",
            "file2.txt",
            MediaType.TEXT_PLAIN_VALUE,
            "Spring Boot Testing".getBytes(StandardCharsets.UTF_8)
        );

        List<String> expectedFilePaths = List.of(
            "/resources/files/boards/file1.txt",
            "/resources/files/boards/file2.txt"
        );

        when(fileService.saveFiles(any(List.class), eq("boards")))
            .thenReturn(expectedFilePaths);

        // when
        mockMvc.perform(multipart("/api/v1/files/boards")
                .file(file1)
                .file(file2)
                .contentType(MediaType.MULTIPART_FORM_DATA))
            // then
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data").isArray())
            .andExpect(jsonPath("$.data[0]").value("/resources/files/boards/file1.txt"))
            .andExpect(jsonPath("$.data[1]").value("/resources/files/boards/file2.txt"));
    }

    @Test
    @WithMockUser(roles = "USER")
    void 유효하지_않은_입력값으로_게시판_파일을_업로드시_예외가_발생한다() throws Exception {
        // given
        List<String> expectedFilePaths = List.of();

        when(fileService.saveFiles(any(List.class), eq("boards")))
            .thenReturn(expectedFilePaths);

        // when
        mockMvc.perform(multipart("/api/v1/files/boards")
                .contentType(MediaType.MULTIPART_FORM_DATA))
            // then
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data").isArray())
            .andExpect(jsonPath("$.data").isEmpty());
    }

    @Test
    void 인증되지_않은_사용자가_게시판_파일을_업로드시_권한에러가_발생한다() throws Exception {
        // given
        MockMultipartFile file = new MockMultipartFile(
            "multipartFile",
            "file.txt",
            MediaType.TEXT_PLAIN_VALUE,
            "Unauthorized access".getBytes(StandardCharsets.UTF_8)
        );

        // when
        mockMvc.perform(multipart("/api/v1/files/boards")
                .file(file)
                .contentType(MediaType.MULTIPART_FORM_DATA))
            // then
            .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void 정상적으로_파일_목록을_조회한다() throws Exception {
        // given
        Pageable pageable = PageRequest.of(0, 10, Sort.by("createdAt").descending());

        UploadedFileResponseDto file1 = UploadedFileResponseDto.builder()
            .originalFileName("file1.txt")
            .url("/resources/files/file1.txt")
            .fileSize("1.0KB")
            .createdAt(LocalDateTime.parse("2024-10-01T00:00:00"))
            .build();

        UploadedFileResponseDto file2 = UploadedFileResponseDto.builder()
            .originalFileName("file2.txt")
            .url("/resources/files/file2.txt")
            .fileSize("2.0KB")
            .createdAt(LocalDateTime.parse("2024-10-02T00:00:00"))
            .build();

        List<UploadedFileResponseDto> fileList = List.of(file1, file2);
        PagedResponseDto<UploadedFileResponseDto> expectedPagedResponse = new PagedResponseDto<>(
            new PageImpl<>(fileList, pageable, fileList.size())
        );

        when(fileService.getFiles(pageable))
            .thenReturn(expectedPagedResponse);

        // when
        mockMvc.perform(get("/api/v1/files")
                .param("page", "0")
                .param("size", "10")
                .param("sortBy", "createdAt")
                .param("sortDirection", "desc")
                .contentType(MediaType.APPLICATION_JSON))
            // then
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data.currentPage").value(0))
            .andExpect(jsonPath("$.data.hasPrevious").value(false))
            .andExpect(jsonPath("$.data.hasNext").value(false))
            .andExpect(jsonPath("$.data.totalPages").value(1))
            .andExpect(jsonPath("$.data.totalItems").value(2))
            .andExpect(jsonPath("$.data.take").value(2))
            .andExpect(jsonPath("$.data.items").isArray())
            .andExpect(jsonPath("$.data.items[0].originalFileName").value("file1.txt"))
            .andExpect(jsonPath("$.data.items[0].url").value("/resources/files/file1.txt"))
            .andExpect(jsonPath("$.data.items[0].fileSize").value("1.0KB"))
            .andExpect(jsonPath("$.data.items[0].createdAt").value("2024-10-01T00:00:00"))
            .andExpect(jsonPath("$.data.items[1].originalFileName").value("file2.txt"))
            .andExpect(jsonPath("$.data.items[1].url").value("/resources/files/file2.txt"))
            .andExpect(jsonPath("$.data.items[1].fileSize").value("2.0KB"))
            .andExpect(jsonPath("$.data.items[1].createdAt").value("2024-10-02T00:00:00"));
    }

    @Test
    void 인증되지_않은_사용자가_파일_목록을_조회시_권한에러가_발생한다() throws Exception {
        // when
        mockMvc.perform(get("/api/v1/files")
                .param("page", "0")
                .param("size", "10")
                .param("sortBy", "createdAt")
                .param("sortDirection", "desc")
                .contentType(MediaType.APPLICATION_JSON))
            // then
            .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void 정상적으로_파일을_삭제한다() throws Exception {
        // given
        DeleteFileRequestDto requestDto = new DeleteFileRequestDto();
        requestDto.setUrl("/resources/files/file1.txt");

        when(fileService.deleteFile(any(DeleteFileRequestDto.class)))
            .thenReturn(true);

        // when
        mockMvc.perform(delete("/api/v1/files")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto)))
            // then
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data").value(true));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void 파일_삭제시_존재하지_않는_파일_경로를_요청하면_예외가_발생한다() throws Exception {
        // given
        DeleteFileRequestDto requestDto = new DeleteFileRequestDto();
        requestDto.setUrl("/resources/files/nonexistent.txt");

        when(fileService.deleteFile(any(DeleteFileRequestDto.class)))
            .thenThrow(new BaseException(ErrorCode.FILE_DELETE_FAILED, "파일 삭제에 실패했습니다."));

        // when
        mockMvc.perform(delete("/api/v1/files")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto)))
            // then
            .andExpect(status().isInternalServerError())
            .andExpect(jsonPath("$.success").value(false))
            .andExpect(jsonPath("$.data").isEmpty());
    }

    @Test
    void 인증되지_않은_사용자가_파일을_삭제시_권한에러가_발생한다() throws Exception {
        // given
        DeleteFileRequestDto requestDto = new DeleteFileRequestDto();
        requestDto.setUrl("/resources/files/file1.txt");

        // when
        mockMvc.perform(delete("/api/v1/files")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto)))
            // then
            .andExpect(status().isUnauthorized());
    }
}
