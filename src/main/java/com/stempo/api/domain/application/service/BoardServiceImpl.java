package com.stempo.api.domain.application.service;

import com.stempo.api.domain.domain.model.Board;
import com.stempo.api.domain.domain.model.BoardCategory;
import com.stempo.api.domain.domain.model.User;
import com.stempo.api.domain.domain.repository.BoardRepository;
import com.stempo.api.domain.presentation.dto.request.BoardRequestDto;
import com.stempo.api.domain.presentation.dto.request.BoardUpdateRequestDto;
import com.stempo.api.domain.presentation.dto.response.BoardResponseDto;
import com.stempo.api.domain.presentation.mapper.BoardDtoMapper;
import com.stempo.api.global.dto.PagedResponseDto;
import com.stempo.api.global.exception.PermissionDeniedException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BoardServiceImpl implements BoardService {

    private final UserService userService;
    private final BoardRepository repository;
    private final BoardDtoMapper mapper;

    @Override
    @Transactional
    public Long registerBoard(BoardRequestDto requestDto) {
        User user = userService.getCurrentUser();
        Board board = mapper.toDomain(requestDto, user.getDeviceTag());
        board.validateAccessPermissionForNotice(user);
        return repository.save(board).getId();
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResponseDto<BoardResponseDto> getBoardsByCategory(BoardCategory category, Pageable pageable) {
        validateAccessPermissionForSuggestion(category);
        Page<Board> boards = repository.findByCategory(category, pageable);
        return new PagedResponseDto<>(boards.map(mapper::toDto));
    }

    @Override
    @Transactional
    public Long updateBoard(Long boardId, BoardUpdateRequestDto requestDto) {
        User user = userService.getCurrentUser();
        Board board = repository.findByIdOrThrow(boardId);
        board.validateAccessPermission(user);
        board.update(requestDto);
        return repository.save(board).getId();
    }

    @Override
    @Transactional
    public Long deleteBoard(Long boardId) {
        User user = userService.getCurrentUser();
        Board board = repository.findByIdOrThrow(boardId);
        board.validateAccessPermission(user);
        repository.delete(board);
        return board.getId();
    }

    private void validateAccessPermissionForSuggestion(BoardCategory category) {
        User user = userService.getCurrentUser();
        if (category.equals(BoardCategory.SUGGESTION) && !user.isAdmin()) {
            throw new PermissionDeniedException("건의하기는 관리자만 조회할 수 있습니다.");
        }
    }
}
