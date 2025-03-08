package com.stempo.config;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

class AopConfigTest {

    @Test
    void AopConfig빈이_정상적으로_등록된다() {
        // given
        try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(AopConfig.class)) {
            // when
            AopConfig aopConfig = context.getBean(AopConfig.class);
            // then
            assertThat(aopConfig).isNotNull();
        }
    }

    @Test
    void EnableAspectJAutoProxy_애노테이션_속성_proxyTargetClass가_true이다() {
        // given
        // when
        EnableAspectJAutoProxy annotation = AopConfig.class.getAnnotation(EnableAspectJAutoProxy.class);
        // then
        assertThat(annotation).isNotNull();
        assertThat(annotation.proxyTargetClass()).isTrue();
    }
}
