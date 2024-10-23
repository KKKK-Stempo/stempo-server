package com.stempo.config;

import static org.assertj.core.api.Assertions.assertThat;

import com.stempo.test.config.TestConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

@DataJpaTest
@ContextConfiguration(classes = {TestConfig.class})
@Import(JpaConfig.class)
@ActiveProfiles("test")
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
