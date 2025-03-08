package com.stempo.service;

import com.stempo.dto.PagedResponseDto;
import com.stempo.dto.request.BoardRequestDto;
import com.stempo.dto.request.BoardUpdateRequestDto;
import com.stempo.dto.response.BoardResponseDto;
import com.stempo.model.BoardCategory;
import org.springframework.data.domain.Pageable;

public interface BoardService {

    Long registerBoard(String deviceTag, BoardRequestDto requestDto);

    PagedResponseDto<BoardResponseDto> getBoardsByCategory(String deviceTag, BoardCategory category, Pageable pageable);

    Long updateBoard(String deviceTag, Long boardId, BoardUpdateRequestDto requestDto);

    Long deleteBoard(String deviceTag, Long boardId);
}
