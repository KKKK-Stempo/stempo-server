package com.stempo.config;

import static org.assertj.core.api.Assertions.assertThat;

import com.stempo.util.PasswordValidator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = ValidatorConfig.class)
class ValidatorConfigTest {

    @Autowired
    private PasswordValidator passwordValidator;

    @Test
    void passwordValidator_빈이_정상적으로_생성된다() {
        // then
        assertThat(passwordValidator).isNotNull();
    }
}
