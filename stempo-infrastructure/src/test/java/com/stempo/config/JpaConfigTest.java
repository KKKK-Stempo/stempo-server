package com.stempo.config;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootTest(classes = JpaConfig.class)
class JpaConfigTest {

    @Autowired
    private ApplicationContext applicationContext;

    @Test
    void JpaConfig가_정상적으로_동작한다() {
        // then
        assertThat(applicationContext).isNotNull();
        assertThat(applicationContext.getBean(JpaConfig.class)).isNotNull();
    }

    @Test
    void JpaAuditing이_활성화되어_있다() {
        // then
        EnableJpaAuditing enableJpaAuditing = JpaConfig.class.getAnnotation(EnableJpaAuditing.class);
        assertThat(enableJpaAuditing).isNotNull();
    }
}
