package com.stempo.api.domain.application.event;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Component
public class AchievementEventProcessorRegistry {

    private final List<AchievementEventProcessor> processors = new ArrayList<>();

    public void register(AchievementEventProcessor processor) {
        processors.add(processor);
    }

    public List<AchievementEventProcessor> getProcessors() {
        return Collections.unmodifiableList(processors);
    }
}
