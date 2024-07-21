package com.stempo.api.domain.application.event;

import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class AchievementEventDispatcher {

    private final List<AchievementEventProcessor> processors;

    @EventListener
    public void handleAchievementDeletedEvent(AchievementDeletedEvent event) {
        processors.forEach(processor -> processor.processAchievementDeleted(event.getAchievementId()));
    }

    @EventListener
    public void handleAchievementUpdatedEvent(AchievementUpdatedEvent event) {
        processors.forEach(processor -> processor.processAchievementUpdated(event.getAchievementId()));
    }
}
