package com.stempo.event;

import com.stempo.model.Record;
import com.stempo.repository.RecordRepository;
import jakarta.annotation.PostConstruct;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

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
