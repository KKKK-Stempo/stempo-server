package com.stempo.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.stempo.dto.PagedResponseDto;
import com.stempo.dto.request.BoardRequestDto;
import com.stempo.dto.request.BoardUpdateRequestDto;
import com.stempo.dto.response.BoardResponseDto;
import com.stempo.model.BoardCategory;
import com.stempo.service.BoardService;
import com.stempo.test.TestApplication;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = BoardController.class)
@ContextConfiguration(classes = TestApplication.class)
@ActiveProfiles("test")
class BoardControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BoardService boardService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithMockUser(roles = "USER")
    void 정상적으로_게시글을_등록한다() throws Exception {
        // given
        BoardRequestDto requestDto = new BoardRequestDto();
        requestDto.setCategory(BoardCategory.NOTICE);
        requestDto.setTitle("청각 자극을 통한 뇌성마비 환자 보행 패턴 개선 서비스, Stempo.");
        requestDto.setContent("Stempo는 청각 자극을 통한 뇌성마비 환자 보행 패턴 개선 서비스입니다.");
        requestDto.setFileUrls(List.of(
                "/resources/files/947051880039041_19dea234-b6ec-4c4b-bc92-c53c0d921943.wav",
                "/resources/files/boards/1/1030487120626166_1dec3611-c148-4139-bb16-3d2a89ac1dd7.pdf"
        ));

        Long expectedBoardId = 1L;

        when(boardService.registerBoard(any(BoardRequestDto.class)))
                .thenReturn(expectedBoardId);

        // when
        mockMvc.perform(post("/api/v1/boards")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                // then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").value(expectedBoardId));
    }

    @Test
    @WithMockUser(roles = "USER")
    void 유효하지_않은_입력값으로_게시글을_등록시_예외가_발생한다() throws Exception {
        // given
        BoardRequestDto requestDto = new BoardRequestDto();
        requestDto.setCategory(null);
        requestDto.setTitle("");
        requestDto.setContent("");

        // when
        mockMvc.perform(post("/api/v1/boards")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                // then
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.data").isEmpty());
    }

    @Test
    void 인증되지_않은_사용자가_게시글을_등록시_권한에러가_발생한다() throws Exception {
        // given
        BoardRequestDto requestDto = new BoardRequestDto();
        requestDto.setCategory(BoardCategory.NOTICE);
        requestDto.setTitle("청각 자극을 통한 뇌성마비 환자 보행 패턴 개선 서비스, Stempo.");
        requestDto.setContent("Stempo는 청각 자극을 통한 뇌성마비 환자 보행 패턴 개선 서비스입니다.");
        requestDto.setFileUrls(List.of(
                "/resources/files/947051880039041_19dea234-b6ec-4c4b-bc92-c53c0d921943.wav",
                "/resources/files/boards/1/1030487120626166_1dec3611-c148-4139-bb16-3d2a89ac1dd7.pdf"
        ));

        // when
        mockMvc.perform(post("/api/v1/boards")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                // then
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "USER")
    void 정상적으로_카테고리별_게시글을_조회한다() throws Exception {
        // given
        BoardCategory category = BoardCategory.NOTICE;
        Pageable pageable = PageRequest.of(0, 10, Sort.by("createdAt").descending());

        BoardResponseDto board1 = BoardResponseDto.builder()
                .id(1L)
                .deviceTag("490154203237518")
                .category(BoardCategory.NOTICE)
                .title("청각 자극을 통한 뇌성마비 환자 보행 패턴 개선 서비스, Stempo.")
                .content("Stempo는 청각 자극을 통한 뇌성마비 환자 보행 패턴 개선 서비스입니다.")
                .fileUrls(List.of(
                        "/resources/files/947051880039041_19dea234-b6ec-4c4b-bc92-c53c0d921943.wav",
                        "/resources/files/boards/1/1030487120626166_1dec3611-c148-4139-bb16-3d2a89ac1dd7.pdf"
                ))
                .createdAt("2024-01-01T00:00:00")
                .build();

        BoardResponseDto board2 = BoardResponseDto.builder()
                .id(2L)
                .deviceTag("490154203237518")
                .category(BoardCategory.NOTICE)
                .title("새로운 공지사항 업데이트")
                .content("새로운 공지사항이 업데이트되었습니다.")
                .fileUrls(List.of())
                .createdAt("2024-01-02T00:00:00")
                .build();

        List<BoardResponseDto> boardList = List.of(board1, board2);
        PagedResponseDto<BoardResponseDto> expectedPagedResponse = new PagedResponseDto<>(
                new PageImpl<>(boardList, pageable, boardList.size())
        );

        Mockito.when(boardService.getBoardsByCategory(eq(category), eq(pageable)))
                .thenReturn(expectedPagedResponse);

        // when
        mockMvc.perform(get("/api/v1/boards")
                        .param("category", "NOTICE")
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
                .andExpect(jsonPath("$.data.items[0].id").value(1))
                .andExpect(jsonPath("$.data.items[0].deviceTag").value("490154203237518"))
                .andExpect(jsonPath("$.data.items[0].category").value("NOTICE"))
                .andExpect(jsonPath("$.data.items[0].title").value("청각 자극을 통한 뇌성마비 환자 보행 패턴 개선 서비스, Stempo."))
                .andExpect(jsonPath("$.data.items[0].content").value("Stempo는 청각 자극을 통한 뇌성마비 환자 보행 패턴 개선 서비스입니다."))
                .andExpect(jsonPath("$.data.items[0].fileUrls").isArray())
                .andExpect(jsonPath("$.data.items[0].fileUrls[0]").value(
                        "/resources/files/947051880039041_19dea234-b6ec-4c4b-bc92-c53c0d921943.wav"))
                .andExpect(jsonPath("$.data.items[0].fileUrls[1]").value(
                        "/resources/files/boards/1/1030487120626166_1dec3611-c148-4139-bb16-3d2a89ac1dd7.pdf"))
                .andExpect(jsonPath("$.data.items[0].createdAt").value("2024-01-01T00:00:00"))
                .andExpect(jsonPath("$.data.items[1].id").value(2))
                .andExpect(jsonPath("$.data.items[1].deviceTag").value("490154203237518"))
                .andExpect(jsonPath("$.data.items[1].category").value("NOTICE"))
                .andExpect(jsonPath("$.data.items[1].title").value("새로운 공지사항 업데이트"))
                .andExpect(jsonPath("$.data.items[1].content").value("새로운 공지사항이 업데이트되었습니다."))
                .andExpect(jsonPath("$.data.items[1].fileUrls").isArray())
                .andExpect(jsonPath("$.data.items[1].fileUrls").isEmpty())
                .andExpect(jsonPath("$.data.items[1].createdAt").value("2024-01-02T00:00:00"));
    }

    @Test
    void 인증되지_않은_사용자가_카테고리별_게시글을_조회시_권한에러가_발생한다() throws Exception {
        // when
        mockMvc.perform(get("/api/v1/boards")
                        .param("category", "NOTICE")
                        .param("page", "0")
                        .param("size", "10")
                        .param("sortBy", "createdAt")
                        .param("sortDirection", "desc")
                        .contentType(MediaType.APPLICATION_JSON))
                // then
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "USER")
    void 정상적으로_게시글을_수정한다() throws Exception {
        // given
        Long boardId = 1L;
        BoardUpdateRequestDto updateRequestDto = new BoardUpdateRequestDto();
        updateRequestDto.setCategory(BoardCategory.NOTICE);
        updateRequestDto.setTitle("청각 자극을 통한 뇌성마비 환자 보행 패턴 개선 서비스, Stempo. (수정)");
        updateRequestDto.setContent("Stempo는 청각 자극을 통한 뇌성마비 환자 보행 패턴 개선 서비스입니다. (수정)");
        updateRequestDto.setFileUrls(List.of(
                "/resources/files/947051880039041_19dea234-b6ec-4c4b-bc92-c53c0d921943.wav"
        ));

        Long expectedBoardId = 1L;

        when(boardService.updateBoard(eq(boardId), any(BoardUpdateRequestDto.class)))
                .thenReturn(expectedBoardId);

        // when
        mockMvc.perform(patch("/api/v1/boards/{boardId}", boardId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequestDto)))
                // then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").value(expectedBoardId));
    }

    @Test
    void 인증되지_않은_사용자가_게시글을_수정시_권한에러가_발생한다() throws Exception {
        // given
        Long boardId = 1L;
        BoardUpdateRequestDto updateRequestDto = new BoardUpdateRequestDto();
        updateRequestDto.setCategory(BoardCategory.NOTICE);
        updateRequestDto.setTitle("청각 자극을 통한 뇌성마비 환자 보행 패턴 개선 서비스, Stempo. (수정)");
        updateRequestDto.setContent("Stempo는 청각 자극을 통한 뇌성마비 환자 보행 패턴 개선 서비스입니다. (수정)");
        updateRequestDto.setFileUrls(List.of(
                "/resources/files/947051880039041_19dea234-b6ec-4c4b-bc92-c53c0d921943.wav"
        ));

        // when
        mockMvc.perform(patch("/api/v1/boards/{boardId}", boardId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequestDto)))
                // then
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "USER")
    void 정상적으로_게시글을_삭제한다() throws Exception {
        // given
        Long boardId = 1L;

        when(boardService.deleteBoard(eq(boardId)))
                .thenReturn(boardId);

        // when
        mockMvc.perform(delete("/api/v1/boards/{boardId}", boardId)
                        .contentType(MediaType.APPLICATION_JSON))
                // then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").value(boardId));
    }

    @Test
    void 인증되지_않은_사용자가_게시글을_삭제시_권한에러가_발생한다() throws Exception {
        // given
        Long boardId = 1L;

        // when
        mockMvc.perform(delete("/api/v1/boards/{boardId}", boardId)
                        .contentType(MediaType.APPLICATION_JSON))
                // then
                .andExpect(status().isUnauthorized());
    }
}
