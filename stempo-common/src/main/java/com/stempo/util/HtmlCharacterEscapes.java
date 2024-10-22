package com.stempo.util;

import com.fasterxml.jackson.core.SerializableString;
import com.fasterxml.jackson.core.io.CharacterEscapes;
import com.fasterxml.jackson.core.io.SerializedString;
import org.apache.commons.text.StringEscapeUtils;

public class HtmlCharacterEscapes extends CharacterEscapes {

    private static final char ZERO_WIDTH_JOINER = 0x200D;
    private final int[] asciiEscapes;

    public HtmlCharacterEscapes() {
        asciiEscapes = CharacterEscapes.standardAsciiEscapesForJSON();
        configureCustomEscapes();
    }

    private void configureCustomEscapes() {
        asciiEscapes['<'] = CharacterEscapes.ESCAPE_CUSTOM;
        asciiEscapes['>'] = CharacterEscapes.ESCAPE_CUSTOM;
        asciiEscapes['\"'] = CharacterEscapes.ESCAPE_CUSTOM;
        asciiEscapes['('] = CharacterEscapes.ESCAPE_CUSTOM;
        asciiEscapes[')'] = CharacterEscapes.ESCAPE_CUSTOM;
        asciiEscapes['#'] = CharacterEscapes.ESCAPE_CUSTOM;
        asciiEscapes['\''] = CharacterEscapes.ESCAPE_CUSTOM;
    }

    @Override
    public int[] getEscapeCodesForAscii() {
        return asciiEscapes;
    }

    @Override
    public SerializableString getEscapeSequence(int ch) {
        // ASCII 문자의 경우 HTML 엔티티로 변환
        if (ch < 128 && asciiEscapes[ch] == CharacterEscapes.ESCAPE_CUSTOM) {
            return escapeHtmlCharacter((char) ch);
        }

        // 유니코드 문자 및 제로 너비 조인너 처리
        if (Character.isHighSurrogate((char) ch) || Character.isLowSurrogate((char) ch) || ch == ZERO_WIDTH_JOINER) {
            return new SerializedString("\\u" + String.format("%04x", ch));
        }

        // 나머지 문자는 HTML 이스케이프 처리
        return escapeHtmlCharacter((char) ch);
    }

    // HTML 엔티티로 변환하는 메서드
    private SerializableString escapeHtmlCharacter(char ch) {
        return new SerializedString(StringEscapeUtils.escapeHtml4(Character.toString(ch)));
    }
}
