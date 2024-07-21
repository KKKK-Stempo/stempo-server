package com.stempo.api.domain.application.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class AchievementDeletedEvent extends ApplicationEvent {

    private final Long achievementId;

    public AchievementDeletedEvent(Object source, Long achievementId) {
        super(source);
        this.achievementId = achievementId;
    }
}
