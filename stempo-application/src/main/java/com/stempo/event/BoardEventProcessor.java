package com.stempo.event;

import com.stempo.model.Board;
import com.stempo.repository.BoardRepository;
import jakarta.annotation.PostConstruct;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class BoardEventProcessor implements UserEventProcessor {

    private final BoardRepository boardRepository;
    private final UserEventProcessorRegistry processorRegistry;

    @PostConstruct
    public void init() {
        processorRegistry.register(this);
    }

    @Override
    @Transactional
    public void processUserDeleted(String deviceTag) {
        List<Board> boards = boardRepository.findByDeviceTag(deviceTag);
        boardRepository.deleteAll(boards);
    }
}
