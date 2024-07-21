package com.stempo.api.domain.application.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class AchievementUpdatedEvent extends ApplicationEvent {

    private final Long achievementId;

    public AchievementUpdatedEvent(Object source, Long achievementId) {
        super(source);
        this.achievementId = achievementId;
    }
}
