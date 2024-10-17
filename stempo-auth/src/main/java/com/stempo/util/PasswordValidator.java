package com.stempo.util;

import java.util.regex.Pattern;

public class PasswordValidator {

    // 최소 8자리 이상, 최대 16자리 이하
    private static final String LENGTH_PATTERN = "^.{8,16}$";

    // 영문 대소문자, 숫자, 특수문자 중 3가지 이상 포함
    private static final String CHARACTER_PATTERN = "^(?=.*[a-z])(?=.*[A-Z])(?=.*[0-9])(?=.*[@#$%^&+=!]).{8,16}$";

    // 연속된 숫자나 동일한 문자 3개 이상 금지
    private static final String SEQUENTIAL_PATTERN = "(\\w)\\1\\1";

    private final Pattern lengthPattern;
    private final Pattern characterPattern;
    private final Pattern sequentialPattern;

    public PasswordValidator() {
        this.lengthPattern = Pattern.compile(LENGTH_PATTERN);
        this.characterPattern = Pattern.compile(CHARACTER_PATTERN);
        this.sequentialPattern = Pattern.compile(SEQUENTIAL_PATTERN);
    }

    public boolean isValid(String password, String username) {
        // 비밀번호가 null인 경우는 유효성 검사를 통과
        if (password == null) {
            return true;
        }

        // 비밀번호에 공백이 포함되어 있는지 확인
        if (password.contains(" ")) {
            return false;
        }

        // 비밀번호 길이 확인 (8 ~ 16자리)
        if (!lengthPattern.matcher(password).matches()) {
            return false;
        }

        // 영문 대소문자, 숫자, 특수문자 중 3가지 이상 포함 여부 확인
        if (!characterPattern.matcher(password).matches()) {
            return false;
        }

        // 연속된 숫자나 동일한 문자 3개 이상 확인
        if (sequentialPattern.matcher(password).find()) {
            return false;
        }

        // 비밀번호에 username(아이디)와 4자 이상 동일한 문자 포함 여부 확인
        if (username != null && password.contains(username)) {
            return false;
        }

        return true;
    }
}
