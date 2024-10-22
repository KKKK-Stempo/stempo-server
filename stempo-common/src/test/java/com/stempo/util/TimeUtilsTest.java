package com.stempo.util;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

class TimeUtilsTest {

    @Test
    void 현재_시간을_밀리초로_가져온다() {
        // given
        long before = System.currentTimeMillis();

        // when
        long currentTime = TimeUtils.currentTimeMillis();

        // then
        long after = System.currentTimeMillis();
        assertThat(currentTime).isBetween(before, after); // 현재 시간 범위 내에 있어야 함
    }
}
