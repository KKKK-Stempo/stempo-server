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
        // Given
        String input = "<script>alert('XSS');</script>";

        // When
        String sanitized = xssSanitizer.sanitize(input);

        // Then
        assertThat(sanitized).isEmpty();
    }

    @Test
    void 허용된_링크_태그는_유지된다() {
        // Given
        String input = "<a href='http://example.com'>Link</a>";

        // When
        String sanitized = xssSanitizer.sanitize(input);

        // Then
        assertThat(sanitized).isEqualTo("<a href=\"http://example.com\" rel=\"nofollow\">Link</a>");
    }

    @Test
    void 허용된_형식_태그는_유지된다() {
        // Given
        String input = "<b>Bold Text</b><i>Italic Text</i>";

        // When
        String sanitized = xssSanitizer.sanitize(input);

        // Then
        assertThat(sanitized).isEqualTo("<b>Bold Text</b><i>Italic Text</i>");
    }

    @Test
    void 입력값이_null이면_null을_반환한다() {
        // When
        String sanitized = xssSanitizer.sanitize(null);

        // Then
        assertThat(sanitized).isNull();
    }

    @Test
    void 안전한_문자열은_변경되지_않는다() {
        // Given
        String input = "Safe text";

        // When
        String sanitized = xssSanitizer.sanitize(input);

        // Then
        assertThat(sanitized).isEqualTo("Safe text");
    }
}
