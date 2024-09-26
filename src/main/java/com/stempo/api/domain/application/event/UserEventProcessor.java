package com.stempo.api.domain.application.event;

public interface UserEventProcessor {

    void processUserDeleted(String deviceTag);
}
