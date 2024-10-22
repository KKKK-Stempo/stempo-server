package com.stempo.util;

import org.apache.commons.text.StringEscapeUtils;

public class LogSanitizerUtils {

    private static final int MAX_LENGTH = 500; // 로그 문자열의 최대 길이

    /**
     * 로그에 기록하기 전에 입력 문자열을 안전하게 변환합니다. 새 줄, 캐리지 리턴, 탭 및 특수 문자를 제거하거나 이스케이프 처리합니다.
     *
     * @param input 로그에 기록될 사용자 입력
     * @return 안전한 로그 문자열
     */
    public static String sanitizeForLog(String input) {
        if (input == null) {
            return null;
        }

        // 길이 초과 시 생략
        if (input.length() > MAX_LENGTH) {
            input = input.substring(0, MAX_LENGTH) + "...";
        }

        // 새로운 줄, 캐리지 리턴, 탭을 '_'로 변환
        input = input.replaceAll("[\n\r\t]", "_");

        // 특수 문자 이스케이프 처리
        input = StringEscapeUtils.escapeHtml4(input);

        // SQL 인젝션 방어
        input = escapeSqlCharacters(input);

        return input;
    }

    private static String escapeSqlCharacters(String input) {
        return input.replaceAll("'", "''") // 작은따옴표 이스케이프
                .replaceAll("\"", "\\\\\"") // 큰따옴표 이스케이프
                .replaceAll(";", "_") // 세미콜론 변환
                .replaceAll("--", "_") // SQL 주석 처리
                .replaceAll("/\\*", "_") // 멀티라인 주석 시작
                .replaceAll("\\*/", "_") // 멀티라인 주석 끝
                .replaceAll("//.*", "_"); // 인라인 주석 처리
    }
}
