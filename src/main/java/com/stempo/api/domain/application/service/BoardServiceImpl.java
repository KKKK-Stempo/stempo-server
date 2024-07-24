package com.stempo.api.domain.application.service;

import com.stempo.api.domain.domain.model.Board;
import com.stempo.api.domain.domain.model.BoardCategory;
import com.stempo.api.domain.domain.repository.BoardRepository;
import com.stempo.api.domain.presentation.dto.request.BoardRequestDto;
import com.stempo.api.domain.presentation.dto.request.BoardUpdateRequestDto;
import com.stempo.api.domain.presentation.dto.response.BoardResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BoardServiceImpl implements BoardService {

    private final UserService userService;
    private final BoardRepository repository;

    @Override
    public Long registerBoard(BoardRequestDto requestDto) {
        String deviceTag = userService.getCurrentDeviceTag();
        Board board = BoardRequestDto.toDomain(requestDto, deviceTag);
        return repository.save(board).getId();
    }

    @Override
    public List<BoardResponseDto> getBoardsByCategory(BoardCategory category) {
        List<Board> boards = repository.findByCategory(category);
        return boards.stream()
                .map(BoardResponseDto::toDto)
                .toList();
    }

    @Override
    public Long updateBoard(Long boardId, BoardUpdateRequestDto requestDto) {
        Board board = repository.findByIdOrThrow(boardId);
        board.update(requestDto);
        return repository.save(board).getId();
    }

    @Override
    public Long deleteBoard(Long boardId) {
        Board board = repository.findByIdOrThrow(boardId);
        board.delete();
        return repository.save(board).getId();
    }
}
