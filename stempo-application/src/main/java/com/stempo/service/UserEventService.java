package com.stempo.service;

import com.stempo.event.UserDeletedEvent;
import com.stempo.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserEventService {

    private final UserService userService;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public String unregisterUser() {
        User user = userService.getCurrentUser();
        userService.delete(user);
        eventPublisher.publishEvent(new UserDeletedEvent(this, user.getDeviceTag()));
        return user.getDeviceTag();
    }
}
