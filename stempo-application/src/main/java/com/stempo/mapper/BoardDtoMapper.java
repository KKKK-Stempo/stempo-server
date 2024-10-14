package com.stempo.mapper;

import com.stempo.dto.request.BoardRequestDto;
import com.stempo.dto.request.BoardUpdateRequestDto;
import com.stempo.dto.response.BoardResponseDto;
import com.stempo.model.Board;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

@Component
public class BoardDtoMapper {

    public Board toDomain(BoardRequestDto requestDto, String deviceTag) {
        return Board.builder()
                .deviceTag(deviceTag)
                .category(requestDto.getCategory())
                .title(requestDto.getTitle())
                .content(requestDto.getContent())
                .fileUrls(requestDto.getFileUrls() == null ? new ArrayList<>() : requestDto.getFileUrls())
                .build();
    }

    public Board toDomain(BoardUpdateRequestDto requestDto) {
        return Board.builder()
                .category(requestDto.getCategory())
                .title(requestDto.getTitle())
                .content(requestDto.getContent())
                .fileUrls(requestDto.getFileUrls() == null ? new ArrayList<>() : requestDto.getFileUrls())
                .build();
    }

    public BoardResponseDto toDto(Board board) {
        return BoardResponseDto.builder()
                .id(board.getId())
                .deviceTag(board.getDeviceTag())
                .category(board.getCategory())
                .title(board.getTitle())
                .content(board.getContent())
                .fileUrls(board.getFileUrls() == null ? new ArrayList<>() : board.getFileUrls())
                .createdAt(board.getCreatedAt().toString())
                .build();
    }
}
