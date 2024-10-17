package com.stempo.controller;

import com.stempo.annotation.SuccessApiResponse;
import com.stempo.dto.ApiResponse;
import com.stempo.dto.PagedResponseDto;
import com.stempo.dto.request.BoardRequestDto;
import com.stempo.dto.request.BoardUpdateRequestDto;
import com.stempo.dto.response.BoardResponseDto;
import com.stempo.exception.InvalidColumnException;
import com.stempo.exception.SortingArgumentException;
import com.stempo.model.BoardCategory;
import com.stempo.service.BoardService;
import com.stempo.util.PageableUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
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
    @SuccessApiResponse(data = "boardId", dataType = Long.class, dataDescription = "게시글 ID")
    @PreAuthorize("hasRole('USER')")
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
    @PreAuthorize("hasRole('USER')")
    @GetMapping("/api/v1/boards")
    public ApiResponse<PagedResponseDto<BoardResponseDto>> getBoardsByCategory(
            @RequestParam(name = "category") BoardCategory category,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "20") int size,
            @RequestParam(name = "sortBy", defaultValue = "createdAt") List<String> sortBy,
            @RequestParam(name = "sortDirection", defaultValue = "desc") List<String> sortDirection
    ) throws InvalidColumnException, SortingArgumentException {
        Pageable pageable = PageableUtils.createPageable(page, size, sortBy, sortDirection, BoardResponseDto.class);
        PagedResponseDto<BoardResponseDto> boards = boardService.getBoardsByCategory(category, pageable);
        return ApiResponse.success(boards);
    }
    
    @Operation(summary = "[U] 게시글 수정", description = "ROLE_USER 이상의 권한이 필요함<br>" +
            "ADMIN은 모든 게시글 수정 가능")
    @SuccessApiResponse(data = "boardId", dataType = Long.class, dataDescription = "게시글 ID")
    @PreAuthorize("hasRole('USER')")
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
    @SuccessApiResponse(data = "boardId", dataType = Long.class, dataDescription = "게시글 ID")
    @PreAuthorize("hasRole('USER')")
    @DeleteMapping("/api/v1/boards/{boardId}")
    public ApiResponse<Long> deleteBoard(
            @PathVariable(name = "boardId") Long boardId
    ) {
        Long id = boardService.deleteBoard(boardId);
        return ApiResponse.success(id);
    }
}
