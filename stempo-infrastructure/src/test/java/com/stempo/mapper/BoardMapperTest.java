package com.stempo.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.stempo.entity.BoardEntity;
import com.stempo.model.Board;
import com.stempo.model.BoardCategory;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class BoardMapperTest {

    private BoardMapper boardMapper;

    @BeforeEach
    void setUp() {
        boardMapper = new BoardMapper();
    }

    @Test
    void 도메인을_엔티티로_매핑한다() {
        // given
        Board board = Board.builder()
                .id(1L)
                .deviceTag("device123")
                .category(BoardCategory.NOTICE)
                .title("Test Title")
                .content("Test Content")
                .fileUrls(List.of("file1.jpg", "file2.jpg"))
                .createdAt(LocalDateTime.now())
                .build();

        // when
        BoardEntity entity = boardMapper.toEntity(board);

        // then
        assertThat(entity.getId()).isEqualTo(board.getId());
        assertThat(entity.getDeviceTag()).isEqualTo(board.getDeviceTag());
        assertThat(entity.getCategory()).isEqualTo(board.getCategory());
        assertThat(entity.getTitle()).isEqualTo(board.getTitle());
        assertThat(entity.getContent()).isEqualTo(board.getContent());
        assertThat(entity.getFileUrls()).isEqualTo(board.getFileUrls());
    }

    @Test
    void 엔티티를_도메인으로_매핑한다() {
        // given
        BoardEntity entity = BoardEntity.builder()
                .id(1L)
                .deviceTag("device123")
                .category(BoardCategory.NOTICE)
                .title("Test Title")
                .content("Test Content")
                .fileUrls(List.of("file1.jpg", "file2.jpg"))
                .build();
        entity.setCreatedAt(LocalDateTime.now());

        // when
        Board board = boardMapper.toDomain(entity);

        // then
        assertThat(board.getId()).isEqualTo(entity.getId());
        assertThat(board.getDeviceTag()).isEqualTo(entity.getDeviceTag());
        assertThat(board.getCategory()).isEqualTo(entity.getCategory());
        assertThat(board.getTitle()).isEqualTo(entity.getTitle());
        assertThat(board.getContent()).isEqualTo(entity.getContent());
        assertThat(board.getFileUrls()).isEqualTo(entity.getFileUrls());
        assertThat(board.getCreatedAt()).isEqualTo(entity.getCreatedAt());
    }

    @Test
    void 엔티티를_도메인으로_매핑할_때_fileUrls가_null인_경우_빈_리스트로_변환된다() {
        // given
        BoardEntity entity = BoardEntity.builder()
                .id(1L)
                .deviceTag("device123")
                .category(BoardCategory.NOTICE)
                .title("Test Title")
                .content("Test Content")
                .fileUrls(null)
                .build();
        entity.setCreatedAt(LocalDateTime.now());

        // when
        Board board = boardMapper.toDomain(entity);

        // then
        assertThat(board.getFileUrls()).isNotNull();
        assertThat(board.getFileUrls()).isEmpty();
    }

    @Test
    void 도메인을_엔티티로_매핑할_때_fileUrls가_null인_경우_빈_리스트로_변환된다() {
        // given
        Board board = Board.builder()
                .id(1L)
                .deviceTag("device123")
                .category(BoardCategory.NOTICE)
                .title("Test Title")
                .content("Test Content")
                .fileUrls(null)
                .createdAt(LocalDateTime.now())
                .build();

        // when
        BoardEntity entity = boardMapper.toEntity(board);

        // then
        assertThat(entity.getFileUrls()).isNotNull();
        assertThat(entity.getFileUrls()).isEmpty();
    }
}
