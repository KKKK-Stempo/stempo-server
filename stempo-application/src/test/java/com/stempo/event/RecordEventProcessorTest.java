package com.stempo.event;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.stempo.model.Record;
import com.stempo.repository.RecordRepository;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class RecordEventProcessorTest {

    @Mock
    private RecordRepository recordRepository;

    private UserEventProcessorRegistry processorRegistry;
    private RecordEventProcessor recordEventProcessor;

    @BeforeEach
    void setUp() {
        processorRegistry = new UserEventProcessorRegistry();
        recordEventProcessor = new RecordEventProcessor(recordRepository, processorRegistry);
        processorRegistry.register(recordEventProcessor);
    }

    @Test
    void 사용자가_삭제되면_기록도_삭제된다() {
        // given
        String deviceTag = "test-device";
        Record record1 = Record.builder().build();
        Record record2 = Record.builder().build();
        List<Record> records = Arrays.asList(record1, record2);

        when(recordRepository.findByDeviceTag(deviceTag)).thenReturn(records);

        // when
        recordEventProcessor.processUserDeleted(deviceTag);

        // then
        verify(recordRepository).findByDeviceTag(deviceTag);
        verify(recordRepository).deleteAll(records);

        assertThat(processorRegistry.getProcessors()).contains(recordEventProcessor);
    }
}
