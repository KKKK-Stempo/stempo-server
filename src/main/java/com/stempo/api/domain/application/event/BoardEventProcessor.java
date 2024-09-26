package com.stempo.api.domain.application.event;

import com.stempo.api.domain.domain.model.Board;
import com.stempo.api.domain.domain.repository.BoardRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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
