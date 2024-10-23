package com.stempo.event;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class UserDeletedEventTest {

    @Test
    void UserDeletedEvent_생성_테스트() {
        // given
        String deviceTag = "testDeviceTag";
        Object source = new Object();

        // when
        UserDeletedEvent event = new UserDeletedEvent(source, deviceTag);

        // then
        assertThat(event.getDeviceTag()).isEqualTo(deviceTag);
        assertThat(event.getSource()).isEqualTo(source);
    }
}
