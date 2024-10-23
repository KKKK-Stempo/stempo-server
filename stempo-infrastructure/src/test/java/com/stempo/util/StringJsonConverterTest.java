package com.stempo.util;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class StringJsonConverterTest {

    private StringJsonConverter converter;

    @BeforeEach
    void setUp() {
        converter = new StringJsonConverter();
    }

    @Test
    void 리스트를_JSON_문자열로_변환한다() {
        // given
        List<String> list = Arrays.asList("file1.jpg", "file2.jpg", "file3.jpg");

        // when
        String json = converter.convertToDatabaseColumn(list);

        // then
        assertThat(json).isNotNull();
        assertThat(json).isEqualTo("[\"file1.jpg\",\"file2.jpg\",\"file3.jpg\"]");
    }

    @Test
    void JSON_문자열을_리스트로_변환한다() {
        // given
        String json = "[\"file1.jpg\",\"file2.jpg\",\"file3.jpg\"]";

        // when
        List<String> list = converter.convertToEntityAttribute(json);

        // then
        assertThat(list).isNotNull();
        assertThat(list).containsExactly("file1.jpg", "file2.jpg", "file3.jpg");
    }

    @Test
    void 잘못된_JSON_문자열을_리스트로_변환할_때_null을_반환한다() {
        // given
        String invalidJson = "invalid json";

        // when
        List<String> list = converter.convertToEntityAttribute(invalidJson);

        // then
        assertThat(list).isNull();
    }

    @Test
    void 리스트를_변환할_때_예외가_발생하면_null을_반환한다() {
        // given
        List<String> invalidList = null;

        // when
        String result = converter.convertToDatabaseColumn(invalidList);

        // then
        assertThat(result).isNull();
    }
}
