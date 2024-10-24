package com.stempo.event;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class UserEventDispatcherTest {

    private UserEventDispatcher dispatcher;
    private UserEventProcessor processor;

    @BeforeEach
    void setUp() {
        processor = mock(UserEventProcessor.class);
        dispatcher = new UserEventDispatcher(List.of(processor));
    }

    @Test
    void UserDeletedEvent를_처리한다() {
        // given
        String deviceTag = "testDeviceTag";
        UserDeletedEvent event = new UserDeletedEvent(this, deviceTag);

        // when
        dispatcher.handleUserDeletedEvent(event);

        // then
        verify(processor, times(1)).processUserDeleted(deviceTag);
    }
}
