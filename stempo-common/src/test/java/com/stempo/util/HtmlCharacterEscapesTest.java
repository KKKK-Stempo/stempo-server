package com.stempo.util;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.core.SerializableString;
import org.apache.commons.text.StringEscapeUtils;
import org.junit.jupiter.api.Test;

class HtmlCharacterEscapesTest {

    private final HtmlCharacterEscapes htmlCharacterEscapes = new HtmlCharacterEscapes();

    @Test
    void 이스케이프된_ASCII_문자가_정상적으로_출력된다() {
        // given
        char[] specialChars = {'<', '>', '\"', '(', ')', '#', '\''};

        // when, then
        for (char c : specialChars) {
            SerializableString escapeSequence = htmlCharacterEscapes.getEscapeSequence(c);
            assertThat(escapeSequence.getValue()).isEqualTo(StringEscapeUtils.escapeHtml4(Character.toString(c)));
        }
    }

    @Test
    void 일반_ASCII_문자도_정상적으로_출력된다() {
        // given
        char[] normalChars = {'a', 'b', '1', ' ', '@'};

        // when, then
        for (char c : normalChars) {
            SerializableString escapeSequence = htmlCharacterEscapes.getEscapeSequence(c);
            assertThat(escapeSequence.getValue()).isEqualTo(Character.toString(c));
        }
    }

    @Test
    void 이스케이프된_유니코드_문자가_정상적으로_출력된다() {
        // given
        char emojiChar = '\uD83D';
        char emojiChar2 = '\uDE00';

        // when
        SerializableString escapeSequence1 = htmlCharacterEscapes.getEscapeSequence(emojiChar);
        SerializableString escapeSequence2 = htmlCharacterEscapes.getEscapeSequence(emojiChar2);

        // then
        assertThat(escapeSequence1.getValue()).isEqualTo("\\u" + String.format("%04x", (int) emojiChar));
        assertThat(escapeSequence2.getValue()).isEqualTo("\\u" + String.format("%04x", (int) emojiChar2));
    }

    @Test
    void 제로_너비_조인너_이스케이프가_정상적으로_출력된다() {
        // given
        char zeroWidthJoiner = 0x200D;

        // when
        SerializableString escapeSequence = htmlCharacterEscapes.getEscapeSequence(zeroWidthJoiner);

        // then
        assertThat(escapeSequence.getValue()).isEqualTo("\\u200d");
    }

    @Test
    void 일반_유니코드_문자가_정상적으로_출력된다() {
        // given
        char regularUnicodeChar = 'A';

        // when
        SerializableString escapeSequence = htmlCharacterEscapes.getEscapeSequence(regularUnicodeChar);

        // then
        assertThat(escapeSequence.getValue()).isEqualTo("A");
    }
}
