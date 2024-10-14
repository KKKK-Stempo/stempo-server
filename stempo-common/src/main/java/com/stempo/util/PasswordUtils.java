package com.stempo.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Component
public class PasswordUtils {

    private static final String UPPER = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String LOWER = "abcdefghijklmnopqrstuvwxyz";
    private static final String DIGITS = "0123456789";
    private static final String SPECIAL = "!@#$%^&*()_+-=[]{}|;:,.<>?";
    private static final String ALL = UPPER + LOWER + DIGITS + SPECIAL;
    private static final SecureRandom RANDOM = new SecureRandom();

    @Value("${security.account.password-length}")
    private int passwordLength;

    public String generateStrongPassword() {
        List<Character> passwordChars = new ArrayList<>();

        // 적어도 하나의 대문자, 소문자, 숫자, 특수문자를 포함
        passwordChars.add(UPPER.charAt(RANDOM.nextInt(UPPER.length())));
        passwordChars.add(LOWER.charAt(RANDOM.nextInt(LOWER.length())));
        passwordChars.add(DIGITS.charAt(RANDOM.nextInt(DIGITS.length())));
        passwordChars.add(SPECIAL.charAt(RANDOM.nextInt(SPECIAL.length())));

        // 나머지 길이만큼 임의의 문자로 채움
        for (int i = 4; i < passwordLength; i++) {
            passwordChars.add(ALL.charAt(RANDOM.nextInt(ALL.length())));
        }

        Collections.shuffle(passwordChars, RANDOM);

        StringBuilder password = new StringBuilder(passwordLength);
        for (char c : passwordChars) {
            password.append(c);
        }

        return password.toString();
    }
}
