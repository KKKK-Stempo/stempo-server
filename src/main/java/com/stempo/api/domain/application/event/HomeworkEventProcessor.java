package com.stempo.api.domain.application.event;

import com.stempo.api.domain.domain.model.Homework;
import com.stempo.api.domain.domain.repository.HomeworkRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
public class HomeworkEventProcessor implements UserEventProcessor {

    private final HomeworkRepository homeworkRepository;
    private final UserEventProcessorRegistry processorRegistry;

    @PostConstruct
    public void init() {
        processorRegistry.register(this);
    }

    @Override
    @Transactional
    public void processUserDeleted(String deviceTag) {
        List<Homework> records = homeworkRepository.findByDeviceTag(deviceTag);
        homeworkRepository.deleteAll(records);
    }
}
