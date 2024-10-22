package com.stempo.util;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class LogSanitizerUtilsTest {

    @Test
    void 기본_문자열이_정상적으로_처리된다() {
        // given
        String input = "Hello World! This is a test.";

        // when
        String sanitized = LogSanitizerUtils.sanitizeForLog(input);

        // then
        assertThat(sanitized).isEqualTo(input);
    }

    @Test
    void 줄바꿈_문자가_존재하는_경우_안전하게_변환한다() {
        // given
        String input = "Hello\nWorld! This is a test.";

        // when
        String sanitized = LogSanitizerUtils.sanitizeForLog(input);

        // then
        assertThat(sanitized).isEqualTo("Hello_World! This is a test.");
    }

    @Test
    void SQL_특수_문자가_정상적으로_이스케이프된다() {
        // given
        String input = "Select * from users where name = 'O'Reilly' and age = 25; -- comment";

        // when
        String sanitized = LogSanitizerUtils.sanitizeForLog(input);

        // then
        assertThat(sanitized).isEqualTo("Select * from users where name = ''O''Reilly'' and age = 25_ _ comment");
    }

    @Test
    void HTML_특수_문자가_정상적으로_이스케이프된다() {
        // given
        String input = "<script>alert('XSS');</script>";

        // when
        String sanitized = LogSanitizerUtils.sanitizeForLog(input);

        // then
        assertThat(sanitized).isEqualTo("&lt_script&gt_alert(''XSS'')_&lt_/script&gt_");
    }

    @Test
    void 길이_제한을_초과하는_문자열은_생략된다() {
        // given
        String input = "A".repeat(600);

        // when
        String sanitized = LogSanitizerUtils.sanitizeForLog(input);

        // then
        assertThat(sanitized.length()).isLessThanOrEqualTo(503); // 최대 500자 + 3자 생략 기호
        assertThat(sanitized).endsWith("...");
    }

    @Test
    void null_값은_null로_반환된다() {
        // when
        String sanitized = LogSanitizerUtils.sanitizeForLog(null);

        // then
        assertThat(sanitized).isNull();
    }
}
