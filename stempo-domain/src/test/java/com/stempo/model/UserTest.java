package com.stempo.model;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class UserTest {

    private User user;

    @BeforeEach
    void setUp() {
        user = User.create("DEVICE_TAG", "PASSWORD");
    }

    @Test
    void 사용자가_정상적으로_생성되는지_확인한다() {
        assertThat(user).isNotNull();
        assertThat(user.getDeviceTag()).isEqualTo("DEVICE_TAG");
        assertThat(user.getPassword()).isEqualTo("PASSWORD");
        assertThat(user.getFailedLoginAttempts()).isEqualTo(0);
        assertThat(user.isAccountLocked()).isFalse();
        assertThat(user.getRole()).isEqualTo(Role.USER);
    }

    @Test
    void 로그인_실패_횟수를_증가시킬_수_있다() {
        // when
        user.incrementFailedLoginAttempts();
        user.incrementFailedLoginAttempts();

        // then
        assertThat(user.getFailedLoginAttempts()).isEqualTo(2);
    }

    @Test
    void 로그인_실패_횟수를_초기화할_수_있다() {
        // given
        user.incrementFailedLoginAttempts();
        user.incrementFailedLoginAttempts();

        // when
        user.resetFailedLoginAttempts();

        // then
        assertThat(user.getFailedLoginAttempts()).isEqualTo(0);
        assertThat(user.isAccountLocked()).isFalse();
    }

    @Test
    void 계정을_잠글_수_있다() {
        // when
        user.lockAccount();

        // then
        assertThat(user.isAccountLocked()).isTrue();
    }

    @Test
    void 계정_잠금을_해제할_수_있다() {
        // given
        user.lockAccount();

        // when
        user.resetFailedLoginAttempts();

        // then
        assertThat(user.isAccountLocked()).isFalse();
    }

    @Test
    void 관리자_계정인지_확인할_수_있다() {
        // given
        user.setRole(Role.ADMIN);

        // then
        assertThat(user.isAdmin()).isTrue();
    }

    @Test
    void 일반_사용자는_관리자가_아니다() {
        // when
        user.setRole(Role.USER);

        // then
        assertThat(user.isAdmin()).isFalse();
    }
}
