package com.stempo.model;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class HomeworkTest {

    private Homework homework;

    @BeforeEach
    void setUp() {
        homework = Homework.create("DEVICE_TAG", "DESCRIPTION");
    }

    @Test
    void 숙제가_정상적으로_생성되는지_확인한다() {
        assertThat(homework).isNotNull();
        assertThat(homework.getDeviceTag()).isEqualTo("DEVICE_TAG");
        assertThat(homework.getDescription()).isEqualTo("DESCRIPTION");
        assertThat(homework.getCompleted()).isFalse();
    }

    @Test
    void 숙제를_수정할_수_있다() {
        // given
        Homework updateHomework = Homework.builder()
                .description("UPDATED_DESCRIPTION")
                .completed(true)
                .build();

        // when
        homework.update(updateHomework);

        // then
        assertThat(homework.getDescription()).isEqualTo("UPDATED_DESCRIPTION");
        assertThat(homework.getCompleted()).isTrue();
    }
}
