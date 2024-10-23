package com.stempo.util;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class PasswordValidatorTest {

    private PasswordValidator passwordValidator;

    @BeforeEach
    void setUp() {
        passwordValidator = new PasswordValidator();
    }

    @Test
    void 비밀번호가_유효한_경우() {
        // given
        String password = "Valid123!";
        String username = "user123";

        // when
        boolean isValid = passwordValidator.isValid(password, username);

        // then
        assertThat(isValid).isTrue();
    }

    @Test
    void 비밀번호가_null인_경우_유효성을_통과한다() {
        // given
        String password = null;
        String username = "user123";

        // when
        boolean isValid = passwordValidator.isValid(password, username);

        // then
        assertThat(isValid).isTrue();
    }

    @Test
    void 비밀번호에_공백이_포함된_경우() {
        // given
        String password = "Password 123!";
        String username = "user123";

        // when
        boolean isValid = passwordValidator.isValid(password, username);

        // then
        assertThat(isValid).isFalse();
    }

    @Test
    void 비밀번호_길이가_짧은_경우() {
        // given
        String password = "Abc12!";
        String username = "user123";

        // when
        boolean isValid = passwordValidator.isValid(password, username);

        // then
        assertThat(isValid).isFalse();
    }

    @Test
    void 비밀번호_길이가_긴_경우() {
        // given
        String password = "VeryLongPassword123!";
        String username = "user123";

        // when
        boolean isValid = passwordValidator.isValid(password, username);

        // then
        assertThat(isValid).isFalse();
    }

    @Test
    void 비밀번호에_영문_대소문자_숫자_특수문자가_없는_경우() {
        // given
        String password = "password";
        String username = "user123";

        // when
        boolean isValid = passwordValidator.isValid(password, username);

        // then
        assertThat(isValid).isFalse();
    }

    @Test
    void 비밀번호에_동일한_문자가_세번_연속된_경우() {
        // given
        String password = "Valid123!!!";
        String username = "user123";

        // when
        boolean isValid = passwordValidator.isValid(password, username);

        // then
        assertThat(isValid).isFalse();
    }

    @Test
    void 비밀번호에_아이디의_4자_이상_연속된_문자가_포함된_경우() {
        // given
        String password = "user1234!";
        String username = "user123";

        // when
        boolean isValid = passwordValidator.isValid(password, username);

        // then
        assertThat(isValid).isFalse();
    }

    @Test
    void 비밀번호가_복잡성_요구사항을_충족하는_경우() {
        // given
        String password = "Abcdef123!";
        String username = "user123";

        // when
        boolean isValid = passwordValidator.isValid(password, username);

        // then
        assertThat(isValid).isTrue();
    }
}
