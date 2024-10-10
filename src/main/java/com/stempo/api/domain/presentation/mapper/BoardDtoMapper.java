package com.stempo.api.domain.presentation.mapper;

import com.stempo.api.domain.domain.model.Board;
import com.stempo.api.domain.presentation.dto.request.BoardRequestDto;
import com.stempo.api.domain.presentation.dto.response.BoardResponseDto;
import org.springframework.stereotype.Component;

@Component
public class BoardDtoMapper {

    public Board toDomain(BoardRequestDto requestDto, String deviceTag) {
        return Board.builder()
                .deviceTag(deviceTag)
                .category(requestDto.getCategory())
                .title(requestDto.getTitle())
                .content(requestDto.getContent())
                .fileUrls(requestDto.getFileUrls())
                .build();
    }

    public BoardResponseDto toDto(Board board) {
        return BoardResponseDto.builder()
                .id(board.getId())
                .deviceTag(board.getDeviceTag())
                .category(board.getCategory())
                .title(board.getTitle())
                .content(board.getContent())
                .fileUrls(board.getFileUrls())
                .createdAt(board.getCreatedAt().toString())
                .build();
    }
}
