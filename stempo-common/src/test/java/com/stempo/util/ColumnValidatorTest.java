package com.stempo.util;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class ColumnValidatorTest {

    @Test
    void 존재하는_컬럼을_확인한다() {
        // given
        String columnName = "validField";

        // when
        boolean result = ColumnValidator.isValidColumn(TestDomain.class, columnName);

        // then
        assertThat(result).isTrue();
    }

    @Test
    void 존재하지_않는_컬럼을_확인한다() {
        // given
        String columnName = "nonExistingField";

        // when
        boolean result = ColumnValidator.isValidColumn(TestDomain.class, columnName);

        // then
        assertThat(result).isFalse();
    }

    @Test
    void 다른_타입의_필드를_확인한다() {
        // given
        String columnName = "anotherField";

        // when
        boolean result = ColumnValidator.isValidColumn(TestDomain.class, columnName);

        // then
        assertThat(result).isTrue();
    }

    @Test
    void null_컬럼_이름을_확인한다() {
        // given
        String columnName = null;

        // when
        boolean result = ColumnValidator.isValidColumn(TestDomain.class, columnName);

        // then
        assertThat(result).isFalse();
    }

    static class TestDomain {

        @SuppressWarnings("unused")
        private String validField;

        @SuppressWarnings("unused")
        private int anotherField;
    }
}
