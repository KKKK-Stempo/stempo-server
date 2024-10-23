package com.stempo.config;

import static org.assertj.core.api.Assertions.assertThat;

import com.warrenstrange.googleauth.GoogleAuthenticator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;

@SpringBootTest(classes = AuthenticatorConfig.class)
class AuthenticatorConfigTest {

    @Autowired
    private ApplicationContext applicationContext;

    @Test
    void googleAuthenticator_빈이_정상적으로_생성된다() {
        // given, when
        GoogleAuthenticator googleAuthenticator = applicationContext.getBean(GoogleAuthenticator.class);

        // then
        assertThat(googleAuthenticator).isNotNull();
    }
}
