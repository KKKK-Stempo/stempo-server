package com.stempo.api.domain.presentation;

import com.stempo.api.domain.application.service.BoardService;
import com.stempo.api.domain.domain.model.BoardCategory;
import com.stempo.api.domain.presentation.dto.request.BoardRequestDto;
import com.stempo.api.domain.presentation.dto.request.BoardUpdateRequestDto;
import com.stempo.api.domain.presentation.dto.response.BoardResponseDto;
import com.stempo.api.global.dto.ApiResponse;
import com.stempo.api.global.dto.PagedResponseDto;
import com.stempo.api.global.exception.InvalidColumnException;
import com.stempo.api.global.exception.SortingArgumentException;
import com.stempo.api.global.util.PageableUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
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
    private final PageableUtil pageableUtil;

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
            "건의하기는 ROLE_ADMIN 이상의 권한이 필요함<br>" +
            "DTO의 필드명을 기준으로 정렬 가능하며, 정렬 방향은 오름차순(asc)과 내림차순(desc)이 가능함")
    @Secured({ "ROLE_USER", "ROLE_ADMIN" })
    @GetMapping("/api/v1/boards")
    public ApiResponse<PagedResponseDto<BoardResponseDto>> getBoardsByCategory(
            @RequestParam(name = "category") BoardCategory category,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "20") int size,
            @RequestParam(name = "sortBy", defaultValue = "createdAt") List<String> sortBy,
            @RequestParam(name = "sortDirection", defaultValue = "desc") List<String> sortDirection
    ) throws InvalidColumnException, SortingArgumentException {
        Pageable pageable = pageableUtil.createPageable(page, size, sortBy, sortDirection, BoardResponseDto.class);
        PagedResponseDto<BoardResponseDto> boards = boardService.getBoardsByCategory(category, pageable);
        return ApiResponse.success(boards);
    }
    
    @Operation(summary = "[U] 게시글 수정", description = "ROLE_USER 이상의 권한이 필요함<br>" +
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
