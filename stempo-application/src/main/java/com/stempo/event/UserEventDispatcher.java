package com.stempo.event;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserEventDispatcher {

    private final List<UserEventProcessor> processors;

    @EventListener
    public void handleUserDeletedEvent(UserDeletedEvent event) {
        processors.forEach(processor -> processor.processUserDeleted(event.getDeviceTag()));
    }
}
