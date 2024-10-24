package com.stempo.event;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class UserEventProcessorRegistryTest {

    private UserEventProcessorRegistry registry;

    @BeforeEach
    void setUp() {
        registry = new UserEventProcessorRegistry();
    }

    @Test
    void 프로세서를_등록한다() {
        // given
        UserEventProcessor processor = mock(UserEventProcessor.class);

        // when
        registry.register(processor);

        // then
        List<UserEventProcessor> processors = registry.getProcessors();
        assertThat(processors).hasSize(1).contains(processor);
    }

    @Test
    void 등록된_프로세서를_수정할_수_없다() {
        // given
        UserEventProcessor processor = mock(UserEventProcessor.class);
        registry.register(processor);

        // when
        List<UserEventProcessor> processors = registry.getProcessors();

        // then
        assertThat(processors).isNotNull().isNotEmpty().hasSize(1);
        assertThatThrownBy(() -> processors.add(processor)).isInstanceOf(UnsupportedOperationException.class);
    }
}
