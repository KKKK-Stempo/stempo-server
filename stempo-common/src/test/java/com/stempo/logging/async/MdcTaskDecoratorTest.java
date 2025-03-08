package com.stempo.logging.async;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.slf4j.MDC;

class MdcTaskDecoratorTest {

    private final MdcTaskDecorator decorator = new MdcTaskDecorator();

    @AfterEach
    void tearDown() {
        MDC.clear();
    }

    @Test
    void MDC컨텍스트가_존재할때_정상적으로_전달된다() {
        // given
        Map<String, String> contextMap = new HashMap<>();
        contextMap.put("key", "value");
        MDC.setContextMap(contextMap);
        final String[] capturedValue = new String[1];
        Runnable original = () -> capturedValue[0] = MDC.get("key");

        // when
        Runnable decorated = decorator.decorate(original);
        decorated.run();

        // then
        assertThat(capturedValue[0]).isEqualTo("value");
        assertThat(MDC.getCopyOfContextMap()).isNull();
    }

    @Test
    void MDC컨텍스트가_없을때_정상적으로_실행된다() {
        // given
        MDC.clear();
        final boolean[] wasRun = {false};
        Runnable original = () -> {
            wasRun[0] = true;
            assertThat(MDC.getCopyOfContextMap()).isNull();
        };

        // when
        Runnable decorated = decorator.decorate(original);
        decorated.run();

        // then
        assertThat(wasRun[0]).isTrue();
        assertThat(MDC.getCopyOfContextMap()).isNull();
    }
}
