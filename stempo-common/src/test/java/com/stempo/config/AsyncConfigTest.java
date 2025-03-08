package com.stempo.config;

import static org.assertj.core.api.Assertions.assertThat;

import com.stempo.logging.async.MdcTaskDecorator;
import java.util.concurrent.Executor;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.test.util.ReflectionTestUtils;

class AsyncConfigTest {

    @Test
    void asyncExecutor빈이_정상적으로_생성된다() {
        // given
        try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(AsyncConfig.class)) {
            // when
            Executor executor = context.getBean("asyncExecutor", Executor.class);

            // then
            assertThat(executor).isNotNull();
            assertThat(executor).isInstanceOf(ThreadPoolTaskExecutor.class);
        }
    }

    @Test
    void asyncExecutor설정값이_올바르게_설정된다() {
        // given
        try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(AsyncConfig.class)) {
            ThreadPoolTaskExecutor taskExecutor = context.getBean("asyncExecutor", ThreadPoolTaskExecutor.class);

            // when
            int corePoolSize = taskExecutor.getCorePoolSize();
            int maxPoolSize = taskExecutor.getMaxPoolSize();
            String threadNamePrefix = taskExecutor.getThreadNamePrefix();
            int queueCapacity = taskExecutor.getThreadPoolExecutor().getQueue().remainingCapacity();
            Object taskDecorator = ReflectionTestUtils.getField(taskExecutor, "taskDecorator");

            // then
            assertThat(corePoolSize).isEqualTo(10);
            assertThat(maxPoolSize).isEqualTo(50);
            assertThat(threadNamePrefix).isEqualTo("AsyncExecutor-");
            assertThat(queueCapacity).isEqualTo(100);
            assertThat(taskDecorator).isInstanceOf(MdcTaskDecorator.class);
        }
    }
}
