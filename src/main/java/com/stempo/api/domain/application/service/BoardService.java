package com.stempo.api.domain.application.service;

import com.stempo.api.domain.domain.model.BoardCategory;
import com.stempo.api.domain.presentation.dto.request.BoardRequestDto;
import com.stempo.api.domain.presentation.dto.request.BoardUpdateRequestDto;
import com.stempo.api.domain.presentation.dto.response.BoardResponseDto;

import java.util.List;

public interface BoardService {
    
    Long registerBoard(BoardRequestDto requestDto);

    List<BoardResponseDto> getBoardsByCategory(BoardCategory category);

    Long updateBoard(Long boardId, BoardUpdateRequestDto requestDto);

    Long deleteBoard(Long boardId);
}
