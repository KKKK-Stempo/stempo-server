package com.stempo.event;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.stempo.model.Board;
import com.stempo.repository.BoardRepository;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class BoardEventProcessorTest {

    @Mock
    private BoardRepository boardRepository;

    private UserEventProcessorRegistry processorRegistry;
    private BoardEventProcessor boardEventProcessor;

    @BeforeEach
    void setUp() {
        processorRegistry = new UserEventProcessorRegistry();
        boardEventProcessor = new BoardEventProcessor(boardRepository, processorRegistry);
        processorRegistry.register(boardEventProcessor);
    }

    @Test
    void 사용자가_삭제되면_게시글도_삭제된다() {
        // given
        String deviceTag = "test-device";
        Board board1 = Board.builder().build();
        Board board2 = Board.builder().build();
        List<Board> boards = Arrays.asList(board1, board2);

        when(boardRepository.findByDeviceTag(deviceTag)).thenReturn(boards);

        // when
        boardEventProcessor.processUserDeleted(deviceTag);

        // then
        verify(boardRepository).findByDeviceTag(deviceTag);
        verify(boardRepository).deleteAll(boards);

        assertThat(processorRegistry.getProcessors()).contains(boardEventProcessor);
    }
}
