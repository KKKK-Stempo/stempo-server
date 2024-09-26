package com.stempo.api.domain.application.event;

import com.stempo.api.domain.domain.model.Record;
import com.stempo.api.domain.domain.repository.RecordRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
public class RecordEventProcessor implements UserEventProcessor {

    private final RecordRepository recordRepository;
    private final UserEventProcessorRegistry processorRegistry;

    @PostConstruct
    public void init() {
        processorRegistry.register(this);
    }

    @Override
    @Transactional
    public void processUserDeleted(String deviceTag) {
        List<Record> records = recordRepository.findByDeviceTag(deviceTag);
        recordRepository.deleteAll(records);
    }
}
