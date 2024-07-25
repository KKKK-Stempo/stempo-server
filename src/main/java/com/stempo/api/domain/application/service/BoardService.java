package com.stempo.api.domain.application.service;

import com.stempo.api.domain.domain.model.BoardCategory;
import com.stempo.api.domain.presentation.dto.request.BoardRequestDto;
import com.stempo.api.domain.presentation.dto.request.BoardUpdateRequestDto;
import com.stempo.api.domain.presentation.dto.response.BoardResponseDto;
import com.stempo.api.global.common.dto.PagedResponseDto;
import org.springframework.data.domain.Pageable;

public interface BoardService {

    Long registerBoard(BoardRequestDto requestDto);

    PagedResponseDto<BoardResponseDto> getBoardsByCategory(BoardCategory category, Pageable pageable);

    Long updateBoard(Long boardId, BoardUpdateRequestDto requestDto);

    Long deleteBoard(Long boardId);
}
