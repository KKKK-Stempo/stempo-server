package com.stempo.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.stempo.event.UserDeletedEvent;
import com.stempo.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

@ExtendWith(MockitoExtension.class)
class UserEventServiceTest {

    @Mock
    private UserService userService;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @InjectMocks
    private UserEventService userEventService;

    private User user;

    @BeforeEach
    void setUp() {
        user = User.builder().deviceTag("test-device").build();
    }

    @Test
    void 사용자가_성공적으로_탈퇴하면_이벤트가_발행된다() {
        // given
        when(userService.getCurrentUser()).thenReturn(user);

        // when
        String deviceTag = userEventService.unregisterUser();

        // then
        assertThat(deviceTag).isEqualTo("test-device");
        verify(userService).delete(user);
        verify(eventPublisher).publishEvent(any(UserDeletedEvent.class));
    }
}
