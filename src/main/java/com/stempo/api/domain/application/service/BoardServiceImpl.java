package com.stempo.api.domain.application.service;

import com.stempo.api.domain.domain.model.Board;
import com.stempo.api.domain.domain.model.BoardCategory;
import com.stempo.api.domain.domain.model.User;
import com.stempo.api.domain.domain.repository.BoardRepository;
import com.stempo.api.domain.presentation.dto.request.BoardRequestDto;
import com.stempo.api.domain.presentation.dto.request.BoardUpdateRequestDto;
import com.stempo.api.domain.presentation.dto.response.BoardResponseDto;
import com.stempo.api.global.exception.PermissionDeniedException;
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
        User user = userService.getCurrentUser();
        Board board = BoardRequestDto.toDomain(requestDto, user.getDeviceTag());
        board.validateAccessPermissionForNotice(user);
        return repository.save(board).getId();
    }

    @Override
    public List<BoardResponseDto> getBoardsByCategory(BoardCategory category) {
        validateAccessPermissionForSuggestion(category);
        List<Board> boards = repository.findByCategory(category);
        return boards.stream()
                .map(BoardResponseDto::toDto)
                .toList();
    }

    @Override
    public Long updateBoard(Long boardId, BoardUpdateRequestDto requestDto) {
        User user = userService.getCurrentUser();
        Board board = repository.findByIdOrThrow(boardId);
        board.validateAccessPermission(user);
        board.update(requestDto);
        return repository.save(board).getId();
    }

    @Override
    public Long deleteBoard(Long boardId) {
        User user = userService.getCurrentUser();
        Board board = repository.findByIdOrThrow(boardId);
        board.validateAccessPermission(user);
        board.delete();
        return repository.save(board).getId();
    }

    private void validateAccessPermissionForSuggestion(BoardCategory category) {
        User user = userService.getCurrentUser();
        if (category.equals(BoardCategory.SUGGESTION) && !user.isAdmin()) {
            throw new PermissionDeniedException("건의하기는 관리자만 조회할 수 있습니다.");
        }
    }
}
