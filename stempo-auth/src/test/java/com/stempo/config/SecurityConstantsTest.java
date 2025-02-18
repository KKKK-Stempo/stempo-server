package com.stempo.config;

import static org.assertj.core.api.Assertions.assertThat;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import org.junit.jupiter.api.Test;

class SecurityConstantsTest {

    @Test
    void PERMIT_ALL_상수_값을_확인한다() {
        assertThat(SecurityConstants.PERMIT_ALL)
            .containsExactly("/actuator/health", "/resources/files/**", "/favicon.ico", "/error", "/");
    }

    @Test
    void PERMIT_ALL_API_ENDPOINTS_POST_상수_값을_확인한다() {
        assertThat(SecurityConstants.PERMIT_ALL_API_ENDPOINTS_POST)
            .containsExactly(
                "/api/v1/auth/register",
                "/api/v1/auth/login",
                "/api/v1/auth/two-factor-authentication"
            );
    }

    @Test
    void 생성자가_비공개인지_확인한다() throws Exception {
        Constructor<SecurityConstants> constructor = SecurityConstants.class.getDeclaredConstructor();
        assertThat(Modifier.isPrivate(constructor.getModifiers())).isTrue();
        constructor.setAccessible(true);
        // 인스턴스 생성 시도 (의도적으로 아무런 동작도 하지 않음)
        constructor.newInstance();
    }
}
