package com.stempo.api.domain.application.event;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Component
public class UserEventProcessorRegistry {

    private final List<UserEventProcessor> processors = new ArrayList<>();

    public void register(UserEventProcessor processor) {
        processors.add(processor);
    }

    public List<UserEventProcessor> getProcessors() {
        return Collections.unmodifiableList(processors);
    }
}
