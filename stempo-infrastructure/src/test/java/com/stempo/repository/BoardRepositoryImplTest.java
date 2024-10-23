package com.stempo.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.stempo.entity.BoardEntity;
import com.stempo.exception.NotFoundException;
import com.stempo.mapper.BoardMapper;
import com.stempo.model.Board;
import com.stempo.model.BoardCategory;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

class BoardRepositoryImplTest {

    @Mock
    private BoardJpaRepository boardJpaRepository;

    @Mock
    private BoardMapper boardMapper;

    @InjectMocks
    private BoardRepositoryImpl boardRepository;

    private Board board;
    private BoardEntity boardEntity;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        board = Board.builder()
                .id(1L)
                .deviceTag("device123")
                .category(BoardCategory.NOTICE)
                .title("Test Title")
                .content("Test Content")
                .fileUrls(List.of("file1.jpg", "file2.jpg"))
                .build();

        boardEntity = BoardEntity.builder()
                .id(1L)
                .deviceTag("device123")
                .category(BoardCategory.NOTICE)
                .title("Test Title")
                .content("Test Content")
                .fileUrls(List.of("file1.jpg", "file2.jpg"))
                .build();
    }

    @Test
    void 게시판을_저장한다() {
        // given
        when(boardMapper.toEntity(any(Board.class))).thenReturn(boardEntity);
        when(boardJpaRepository.save(any(BoardEntity.class))).thenReturn(boardEntity);
        when(boardMapper.toDomain(any(BoardEntity.class))).thenReturn(board);

        // when
        Board savedBoard = boardRepository.save(board);

        // then
        assertThat(savedBoard).isNotNull();
        assertThat(savedBoard.getId()).isEqualTo(board.getId());
        verify(boardJpaRepository, times(1)).save(boardEntity);
    }

    @Test
    void 게시판을_삭제한다() {
        // given
        when(boardMapper.toEntity(any(Board.class))).thenReturn(boardEntity);

        // when
        boardRepository.delete(board);

        // then
        verify(boardJpaRepository, times(1)).delete(boardEntity);
    }

    @Test
    void 게시판을_모두_삭제한다() {
        // given
        when(boardMapper.toEntity(any(Board.class))).thenReturn(boardEntity);

        // when
        boardRepository.deleteAll(List.of(board));

        // then
        verify(boardJpaRepository, times(1)).deleteAll(List.of(boardEntity));
    }

    @Test
    void 카테고리로_게시판을_조회한다() {
        // given
        Pageable pageable = PageRequest.of(0, 10);
        Page<BoardEntity> boardEntities = new PageImpl<>(List.of(boardEntity));
        when(boardJpaRepository.findByCategory(any(BoardCategory.class), any(Pageable.class))).thenReturn(
                boardEntities);
        when(boardMapper.toDomain(any(BoardEntity.class))).thenReturn(board);

        // when
        Page<Board> result = boardRepository.findByCategory(BoardCategory.NOTICE, pageable);

        // then
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().getFirst().getCategory()).isEqualTo(BoardCategory.NOTICE);
    }

    @Test
    void ID로_게시판을_조회한다() {
        // given
        when(boardJpaRepository.findById(1L)).thenReturn(Optional.of(boardEntity));
        when(boardMapper.toDomain(any(BoardEntity.class))).thenReturn(board);

        // when
        Board foundBoard = boardRepository.findByIdOrThrow(1L);

        // then
        assertThat(foundBoard).isNotNull();
        assertThat(foundBoard.getId()).isEqualTo(1L);
    }

    @Test
    void ID로_게시판을_조회할_때_존재하지_않으면_예외를_던진다() {
        // given
        when(boardJpaRepository.findById(1L)).thenReturn(Optional.empty());

        // when, then
        assertThrows(NotFoundException.class, () -> boardRepository.findByIdOrThrow(1L));
    }

    @Test
    void 디바이스_태그로_게시판을_조회한다() {
        // given
        when(boardJpaRepository.findByDeviceTag("device123")).thenReturn(List.of(boardEntity));
        when(boardMapper.toDomain(any(BoardEntity.class))).thenReturn(board);

        // when
        List<Board> result = boardRepository.findByDeviceTag("device123");

        // then
        assertThat(result).hasSize(1);
        assertThat(result.getFirst().getDeviceTag()).isEqualTo("device123");
    }
}
