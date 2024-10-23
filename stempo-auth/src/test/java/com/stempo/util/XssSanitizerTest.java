package com.stempo.util;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class XssSanitizerTest {

    private XssSanitizer xssSanitizer;

    @BeforeEach
    void setUp() {
        xssSanitizer = new XssSanitizer();
    }

    @Test
    void XSS_공격_패턴이_제거된다() {
        // given
        String input = "<script>alert('XSS');</script>";

        // when
        String sanitized = xssSanitizer.sanitize(input);

        // then
        assertThat(sanitized).isEmpty();
    }

    @Test
    void 허용된_링크_태그는_유지된다() {
        // given
        String input = "<a href='http://example.com'>Link</a>";

        // when
        String sanitized = xssSanitizer.sanitize(input);

        // then
        assertThat(sanitized).isEqualTo("<a href=\"http://example.com\" rel=\"nofollow\">Link</a>");
    }

    @Test
    void 허용된_형식_태그는_유지된다() {
        // given
        String input = "<b>Bold Text</b><i>Italic Text</i>";

        // when
        String sanitized = xssSanitizer.sanitize(input);

        // then
        assertThat(sanitized).isEqualTo("<b>Bold Text</b><i>Italic Text</i>");
    }

    @Test
    void 입력값이_null이면_null을_반환한다() {
        // when
        String sanitized = xssSanitizer.sanitize(null);

        // then
        assertThat(sanitized).isNull();
    }

    @Test
    void 안전한_문자열은_변경되지_않는다() {
        // given
        String input = "Safe text";

        // when
        String sanitized = xssSanitizer.sanitize(input);

        // then
        assertThat(sanitized).isEqualTo("Safe text");
    }
}
