package com.stempo.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.stempo.dto.PagedResponseDto;
import com.stempo.dto.request.BoardRequestDto;
import com.stempo.dto.request.BoardUpdateRequestDto;
import com.stempo.dto.response.BoardResponseDto;
import com.stempo.exception.PermissionDeniedException;
import com.stempo.mapper.BoardDtoMapper;
import com.stempo.model.Board;
import com.stempo.model.BoardCategory;
import com.stempo.model.User;
import com.stempo.repository.BoardRepository;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

class BoardServiceImplTest {

    @Mock
    private UserService userService;

    @Mock
    private UploadedFileService uploadedFileService;

    @Mock
    private BoardRepository repository;

    @Mock
    private BoardDtoMapper mapper;

    @InjectMocks
    private BoardServiceImpl boardService;

    private BoardRequestDto boardRequestDto;
    private BoardUpdateRequestDto boardUpdateRequestDto;
    private User user;
    private Board board;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        boardRequestDto = new BoardRequestDto();
        boardUpdateRequestDto = new BoardUpdateRequestDto();

        user = User.create("test-device", "test-password");
        board = Board.builder()
                .id(1L)
                .deviceTag("test-device")
                .category(BoardCategory.SUGGESTION)
                .title("test-title")
                .content("test-content")
                .fileUrls(List.of("test-file"))
                .build();

        boardRequestDto.setFileUrls(Arrays.asList("file1.jpg", "file2.jpg"));
        boardUpdateRequestDto.setFileUrls(List.of("file1_updated.jpg"));
    }

    @Test
    void 게시글을_등록한다() {
        // given
        when(userService.getCurrentUser()).thenReturn(user);
        when(mapper.toDomain(any(BoardRequestDto.class), anyString())).thenReturn(board);
        when(repository.save(any(Board.class))).thenReturn(board);

        // when
        Long result = boardService.registerBoard(boardRequestDto);

        // then
        assertThat(result).isEqualTo(board.getId());
        verify(uploadedFileService).verifyFilesExist(anyList());
        verify(repository).save(any(Board.class));
    }

    @Test
    void 권한이_없는_사용자가_건의하기_게시글을_조회하면_예외가_발생한다() {
        // given
        when(userService.getCurrentUser()).thenReturn(user);

        // when & then
        assertThatThrownBy(() -> boardService.getBoardsByCategory(BoardCategory.SUGGESTION, Pageable.unpaged()))
                .isInstanceOf(PermissionDeniedException.class)
                .hasMessage("건의하기는 관리자만 조회할 수 있습니다.");
    }

    @Test
    void 카테고리별로_게시글을_조회한다() {
        // given
        Page<Board> boardsPage = new PageImpl<>(List.of(board));
        when(repository.findByCategory(any(BoardCategory.class), any(Pageable.class))).thenReturn(boardsPage);
        when(mapper.toDto(any(Board.class))).thenReturn(BoardResponseDto.builder().build());

        // when
        PagedResponseDto<BoardResponseDto> result = boardService.getBoardsByCategory(BoardCategory.NOTICE,
                Pageable.unpaged());

        // then
        assertThat(result.getItems()).hasSize(1);
        verify(repository).findByCategory(any(BoardCategory.class), any(Pageable.class));
    }

    @Test
    void 게시글을_수정한다() {
        // given
        when(userService.getCurrentUser()).thenReturn(user);
        when(repository.findByIdOrThrow(anyLong())).thenReturn(board);
        when(mapper.toDomain(any(BoardUpdateRequestDto.class))).thenReturn(board);
        when(repository.save(any(Board.class))).thenReturn(board);

        // when
        Long result = boardService.updateBoard(1L, boardUpdateRequestDto);

        // then
        assertThat(result).isEqualTo(board.getId());
        verify(uploadedFileService).verifyFilesExist(anyList());
        verify(repository).save(any(Board.class));
    }

    @Test
    void 게시글을_삭제한다() {
        // given
        when(userService.getCurrentUser()).thenReturn(user);
        when(repository.findByIdOrThrow(anyLong())).thenReturn(board);

        // when
        Long result = boardService.deleteBoard(1L);

        // then
        assertThat(result).isEqualTo(board.getId());
        verify(repository).delete(any(Board.class));
    }
}
