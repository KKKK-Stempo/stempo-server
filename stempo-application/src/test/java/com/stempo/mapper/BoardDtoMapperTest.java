package com.stempo.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.stempo.dto.request.BoardRequestDto;
import com.stempo.dto.request.BoardUpdateRequestDto;
import com.stempo.dto.response.BoardResponseDto;
import com.stempo.model.Board;
import com.stempo.model.BoardCategory;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class BoardDtoMapperTest {

    private BoardDtoMapper boardDtoMapper;

    @BeforeEach
    void setUp() {
        boardDtoMapper = new BoardDtoMapper();
    }

    @Test
    void BoardRequestDto에서_Board로_매핑된다() {
        // given
        BoardRequestDto requestDto = new BoardRequestDto();
        requestDto.setCategory(BoardCategory.NOTICE);
        requestDto.setTitle("Test Title");
        requestDto.setContent("Test Content");
        List<String> fileUrls = Arrays.asList("/file1", "/file2");
        requestDto.setFileUrls(fileUrls);
        String deviceTag = "device123";

        // when
        Board board = boardDtoMapper.toDomain(requestDto, deviceTag);

        // then
        assertThat(board.getDeviceTag()).isEqualTo(deviceTag);
        assertThat(board.getCategory()).isEqualTo(BoardCategory.NOTICE);
        assertThat(board.getTitle()).isEqualTo("Test Title");
        assertThat(board.getContent()).isEqualTo("Test Content");
        assertThat(board.getFileUrls()).isEqualTo(fileUrls);
    }

    @Test
    void BoardUpdateRequestDto에서_Board로_매핑된다() {
        // given
        BoardUpdateRequestDto updateRequestDto = new BoardUpdateRequestDto();
        updateRequestDto.setCategory(BoardCategory.NOTICE);
        updateRequestDto.setTitle("Updated Title");
        updateRequestDto.setContent("Updated Content");
        List<String> fileUrls = Arrays.asList("/updatedFile1", "/updatedFile2");
        updateRequestDto.setFileUrls(fileUrls);

        // when
        Board board = boardDtoMapper.toDomain(updateRequestDto);

        // then
        assertThat(board.getCategory()).isEqualTo(BoardCategory.NOTICE);
        assertThat(board.getTitle()).isEqualTo("Updated Title");
        assertThat(board.getContent()).isEqualTo("Updated Content");
        assertThat(board.getFileUrls()).isEqualTo(fileUrls);
    }

    @Test
    void Board에서_BoardResponseDto로_매핑된다() {
        // given
        Board board = Board.builder()
                .id(1L)
                .deviceTag("device123")
                .category(BoardCategory.NOTICE)
                .title("Test Title")
                .content("Test Content")
                .fileUrls(Arrays.asList("/file1", "/file2"))
                .createdAt(LocalDateTime.now())
                .build();

        // when
        BoardResponseDto responseDto = boardDtoMapper.toDto(board);

        // then
        assertThat(responseDto.getId()).isEqualTo(1L);
        assertThat(responseDto.getDeviceTag()).isEqualTo("device123");
        assertThat(responseDto.getCategory()).isEqualTo(BoardCategory.NOTICE);
        assertThat(responseDto.getTitle()).isEqualTo("Test Title");
        assertThat(responseDto.getContent()).isEqualTo("Test Content");
        assertThat(responseDto.getFileUrls()).containsExactly("/file1", "/file2");
        assertThat(responseDto.getCreatedAt()).isNotNull();
    }

    @Test
    void BoardRequestDto의_fileUrls가_null이면_빈_리스트로_매핑된다() {
        // given
        BoardRequestDto requestDto = new BoardRequestDto();
        requestDto.setCategory(BoardCategory.NOTICE);
        requestDto.setTitle("Test Title");
        requestDto.setContent("Test Content");
        requestDto.setFileUrls(null);
        String deviceTag = "device123";

        // when
        Board board = boardDtoMapper.toDomain(requestDto, deviceTag);

        // then
        assertThat(board.getFileUrls()).isEmpty();
    }

    @Test
    void BoardUpdateRequestDto의_fileUrls가_null이면_빈_리스트로_매핑된다() {
        // given
        BoardUpdateRequestDto updateRequestDto = new BoardUpdateRequestDto();
        updateRequestDto.setCategory(BoardCategory.NOTICE);
        updateRequestDto.setTitle("Updated Title");
        updateRequestDto.setContent("Updated Content");
        updateRequestDto.setFileUrls(null);

        // when
        Board board = boardDtoMapper.toDomain(updateRequestDto);

        // then
        assertThat(board.getFileUrls()).isEmpty();
    }

    @Test
    void Board의_fileUrls가_null이면_빈_리스트로_매핑된다() {
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
        BoardResponseDto responseDto = boardDtoMapper.toDto(board);

        // then
        assertThat(responseDto.getFileUrls()).isEmpty();
    }
}
