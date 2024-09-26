package com.stempo.api.domain.application.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class UserDeletedEvent extends ApplicationEvent {

    private final String deviceTag;

    public UserDeletedEvent(Object source, String deviceTag) {
        super(source);
        this.deviceTag = deviceTag;
    }
}
