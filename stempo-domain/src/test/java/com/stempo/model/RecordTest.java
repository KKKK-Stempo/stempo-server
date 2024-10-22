package com.stempo.model;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class RecordTest {

    private Record record;

    @BeforeEach
    void setUp() {
        record = Record.create("DEVICE_TAG", "90", "150", "100");
    }

    @Test
    void 기록이_정상적으로_생성되는지_확인한다() {
        assertThat(record.getId()).isNull();
        assertThat(record.getDeviceTag()).isEqualTo("DEVICE_TAG");
        assertThat(record.getAccuracy()).isEqualTo("90");
        assertThat(record.getDuration()).isEqualTo("150");
        assertThat(record.getSteps()).isEqualTo("100");
        assertThat(record.getCreatedAt()).isNull();
    }
}
