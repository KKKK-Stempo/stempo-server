package com.stempo.api.domain.presentation;

import com.stempo.api.domain.application.service.BoardService;
import com.stempo.api.domain.domain.model.BoardCategory;
import com.stempo.api.domain.presentation.dto.request.BoardRequestDto;
import com.stempo.api.domain.presentation.dto.request.BoardUpdateRequestDto;
import com.stempo.api.domain.presentation.dto.response.BoardResponseDto;
import com.stempo.api.global.common.dto.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Tag(name = "Board", description = "게시판")
public class BoardController {

    private final BoardService boardService;

    @Operation(summary = "[U] 게시글 작성", description = "ROLE_USER 이상의 권한이 필요함<br>" +
            "공지사항은 ROLE_ADMIN 이상의 권한이 필요함")
    @Secured({ "ROLE_USER", "ROLE_ADMIN" })
    @PostMapping("/api/v1/boards")
    public ApiResponse<Long> registerBoard(
            @Valid @RequestBody BoardRequestDto requestDto
    ) {
        Long id = boardService.registerBoard(requestDto);
        return ApiResponse.success(id);
    }

    @Operation(summary = "[U] 카테고리별 게시글 조회", description = "ROLE_USER 이상의 권한이 필요함<br>" +
            "건의하기는 본인의 글이 아닌 경우 ROLE_ADMIN 이상의 권한이 필요함")
    @Secured({ "ROLE_USER", "ROLE_ADMIN" })
    @GetMapping("/api/v1/boards")
    public ApiResponse<List<BoardResponseDto>> getBoardsByCategory(
            @RequestParam(name = "category") BoardCategory category
    ) {
        List<BoardResponseDto> boards = boardService.getBoardsByCategory(category);
        return ApiResponse.success(boards);
    }

    @Operation(summary = "[U] 게시글 수정", description = "ROLE_USER 이상의 권한이 필요함<br>" +
            "공지사항은 ROLE_ADMIN 이상의 권한이 필요함<br>" +
            "ADMIN은 모든 게시글 수정 가능")
    @Secured({ "ROLE_USER", "ROLE_ADMIN" })
    @PatchMapping("/api/v1/boards/{boardId}")
    public ApiResponse<Long> updateBoard(
            @PathVariable(name = "boardId") Long boardId,
            @Valid @RequestBody BoardUpdateRequestDto requestDto
    ) {
        Long id = boardService.updateBoard(boardId, requestDto);
        return ApiResponse.success(id);
    }

    @Operation(summary = "[U] 게시글 삭제", description = "ROLE_USER 이상의 권한이 필요함<br>" +
            "공지사항은 ROLE_ADMIN 이상의 권한이 필요함<br>" +
            "ADMIN은 모든 게시글 삭제 가능")
    @Secured({ "ROLE_USER", "ROLE_ADMIN" })
    @DeleteMapping("/api/v1/boards/{boardId}")
    public ApiResponse<Long> deleteBoard(
            @PathVariable(name = "boardId") Long boardId
    ) {
        Long id = boardService.deleteBoard(boardId);
        return ApiResponse.success(id);
    }
}
