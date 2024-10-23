package com.stempo.util;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

public class PasswordValidator {

    // 비밀번호 길이: 최소 8자리 이상, 최대 16자리 이하
    private static final String PASSWORD_LENGTH_PATTERN = "^.{8,16}$";

    // 비밀번호에 영문 대문자, 소문자, 숫자, 특수문자 중 최소 3가지가 포함되어야 함
    private static final String CHARACTER_COMPLEXITY_PATTERN = "^(?=.*[a-z])(?=.*[A-Z])(?=.*[0-9])(?=.*[@#$%^&+=!]).{8,16}$";

    // 연속된 문자나 숫자 3개 이상 금지
    private static final String SEQUENTIAL_CHARACTERS_PATTERN = "(.)\\1\\1";

    // 비밀번호에 사용자 아이디의 4자 이상 연속된 부분 문자열이 포함되면 안됨
    private static final int MIN_USERNAME_SUBSTRING_LENGTH = 4;

    private final Pattern lengthPattern;
    private final Pattern complexityPattern;
    private final Pattern sequentialPattern;

    public PasswordValidator() {
        this.lengthPattern = Pattern.compile(PASSWORD_LENGTH_PATTERN);
        this.complexityPattern = Pattern.compile(CHARACTER_COMPLEXITY_PATTERN);
        this.sequentialPattern = Pattern.compile(SEQUENTIAL_CHARACTERS_PATTERN);
    }

    /**
     * 비밀번호가 유효한지 확인하는 메서드
     *
     * @param password 비밀번호
     * @param username 사용자 아이디 (비밀번호와 비교)
     * @return 비밀번호가 유효하면 true, 그렇지 않으면 false
     */
    public boolean isValid(String password, String username) {
        // 비밀번호가 null인 경우 유효성 검사 통과
        if (password == null) {
            return true;
        }

        // 비밀번호에 공백이 포함되어 있는 경우 무효
        if (password.contains(" ")) {
            return false;
        }

        // 비밀번호 길이 확인 (8 ~ 16자리)
        if (!lengthPattern.matcher(password).matches()) {
            return false;
        }

        // 비밀번호 복잡도 확인 (대문자, 소문자, 숫자, 특수문자 중 3가지 이상 포함)
        if (!complexityPattern.matcher(password).matches()) {
            return false;
        }

        // 동일한 문자나 숫자가 3번 이상 연속되는지 확인
        if (sequentialPattern.matcher(password).find()) {
            return false;
        }

        // 비밀번호에 username의 4자 이상 연속으로 포함된 경우 무효
        return username == null || !containsSubstringOfLength(password, username, MIN_USERNAME_SUBSTRING_LENGTH);
    }

    /**
     * 비밀번호에 사용자 아이디의 length 길이 이상 연속된 부분 문자열이 포함되어 있는지 확인하는 메서드
     */
    private boolean containsSubstringOfLength(String password, String username, int length) {
        int usernameLength = username.length();
        if (usernameLength < length) {
            return false;
        }

        Set<String> substrings = new HashSet<>();
        for (int i = 0; i <= usernameLength - length; i++) {
            substrings.add(username.substring(i, i + length));
        }

        for (String substring : substrings) {
            if (password.contains(substring)) {
                return true;
            }
        }

        return false;
    }
}
