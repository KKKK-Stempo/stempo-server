package com.stempo.service;

import com.stempo.dto.PagedResponseDto;
import com.stempo.dto.request.BoardRequestDto;
import com.stempo.dto.request.BoardUpdateRequestDto;
import com.stempo.dto.response.BoardResponseDto;
import com.stempo.exception.BaseException;
import com.stempo.exception.ErrorCode;
import com.stempo.mapper.BoardDtoMapper;
import com.stempo.model.Board;
import com.stempo.model.BoardCategory;
import com.stempo.model.User;
import com.stempo.repository.BoardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BoardServiceImpl implements BoardService {

    private final UserService userService;
    private final UploadedFileService uploadedFileService;
    private final BoardRepository repository;
    private final BoardDtoMapper mapper;

    @Override
    @Transactional
    public Long registerBoard(String deviceTag, BoardRequestDto requestDto) {
        User user = userService.getById(deviceTag);
        uploadedFileService.verifyFilesExist(requestDto.getFileUrls());
        Board board = mapper.toDomain(requestDto, user.getDeviceTag());
        board.validateAccessPermissionForNotice(user);
        return repository.save(board).getId();
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResponseDto<BoardResponseDto> getBoardsByCategory(
        String deviceTag,
        BoardCategory category,
        Pageable pageable
    ) {
        validateAccessPermissionForSuggestion(deviceTag, category);
        Page<Board> boards = repository.findByCategory(category, pageable);
        return new PagedResponseDto<>(boards.map(mapper::toDto));
    }

    @Override
    @Transactional
    public Long updateBoard(String deviceTag, Long boardId, BoardUpdateRequestDto requestDto) {
        User user = userService.getById(deviceTag);
        Board board = repository.findByIdOrThrow(boardId);

        uploadedFileService.verifyFilesExist(requestDto.getFileUrls());
        board.validateAccessPermission(user);

        Board updatedBoard = mapper.toDomain(requestDto);
        board.update(updatedBoard);
        return repository.save(board).getId();
    }

    @Override
    @Transactional
    public Long deleteBoard(String deviceTag, Long boardId) {
        User user = userService.getById(deviceTag);
        Board board = repository.findByIdOrThrow(boardId);
        board.validateAccessPermission(user);
        repository.delete(board);
        return board.getId();
    }

    private void validateAccessPermissionForSuggestion(String deviceTag, BoardCategory category) {
        User user = userService.getById(deviceTag);
        if (category.equals(BoardCategory.SUGGESTION) && !user.isAdmin()) {
            throw new BaseException(ErrorCode.PERMISSION_DENIED, "건의하기는 관리자만 조회할 수 있습니다.");
        }
    }
}
