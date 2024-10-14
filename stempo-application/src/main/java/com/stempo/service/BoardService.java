package com.stempo.service;

import com.stempo.dto.PagedResponseDto;
import com.stempo.dto.request.BoardRequestDto;
import com.stempo.dto.request.BoardUpdateRequestDto;
import com.stempo.dto.response.BoardResponseDto;
import com.stempo.model.BoardCategory;
import org.springframework.data.domain.Pageable;

public interface BoardService {

    Long registerBoard(BoardRequestDto requestDto);

    PagedResponseDto<BoardResponseDto> getBoardsByCategory(BoardCategory category, Pageable pageable);

    Long updateBoard(Long boardId, BoardUpdateRequestDto requestDto);

    Long deleteBoard(Long boardId);
}
