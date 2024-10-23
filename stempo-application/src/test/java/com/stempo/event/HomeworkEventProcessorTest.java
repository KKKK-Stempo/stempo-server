package com.stempo.event;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.stempo.model.Homework;
import com.stempo.repository.HomeworkRepository;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class HomeworkEventProcessorTest {

    @Mock
    private HomeworkRepository homeworkRepository;

    private UserEventProcessorRegistry processorRegistry;
    private HomeworkEventProcessor homeworkEventProcessor;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        processorRegistry = new UserEventProcessorRegistry();
        homeworkEventProcessor = new HomeworkEventProcessor(homeworkRepository, processorRegistry);
        processorRegistry.register(homeworkEventProcessor);
    }

    @Test
    void 사용자가_삭제되면_과제도_삭제된다() {
        // given
        String deviceTag = "test-device";
        Homework homework1 = Homework.builder().build();
        Homework homework2 = Homework.builder().build();
        List<Homework> homeworks = Arrays.asList(homework1, homework2);

        when(homeworkRepository.findByDeviceTag(deviceTag)).thenReturn(homeworks);

        // when
        homeworkEventProcessor.processUserDeleted(deviceTag);

        // then
        verify(homeworkRepository).findByDeviceTag(deviceTag);
        verify(homeworkRepository).deleteAll(homeworks);

        assertThat(processorRegistry.getProcessors()).contains(homeworkEventProcessor);
    }
}
