package com.stempo.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.stempo.exception.AccountLockedException;
import com.stempo.exception.NotFoundException;
import com.stempo.model.User;
import com.stempo.repository.UserRepository;
import com.stempo.util.AuthUtils;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.BadCredentialsException;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    private final int maxFailedAttempts = 5;
    @Mock
    private UserRepository repository;
    @InjectMocks
    private UserServiceImpl userService;
    private User user;

    @BeforeEach
    void setUp() {
        user = User.create("test-device-tag", "test-password");
    }

    @Test
    void 아이디로_사용자를_찾으면_사용자를_반환한다() {
        // given
        when(repository.findById("test-device-tag")).thenReturn(Optional.of(user));

        // when
        Optional<User> result = userService.findById("test-device-tag");

        // then
        assertThat(result).isPresent().contains(user);
        verify(repository).findById("test-device-tag");
    }

    @Test
    void 아이디가_존재하면_true를_반환한다() {
        // given
        when(repository.existsById("test-device-tag")).thenReturn(true);

        // when
        boolean exists = userService.existsById("test-device-tag");

        // then
        assertThat(exists).isTrue();
        verify(repository).existsById("test-device-tag");
    }

    @Test
    void 아이디로_사용자를_조회하면_사용자를_반환한다() {
        // given
        when(repository.findByIdOrThrow("test-device-tag")).thenReturn(user);

        // when
        User result = userService.getById("test-device-tag");

        // then
        assertThat(result).isEqualTo(user);
        verify(repository).findByIdOrThrow("test-device-tag");
    }

    @Test
    void 존재하지_않는_아이디로_사용자를_조회하면_예외를_던진다() {
        // given
        when(repository.findByIdOrThrow("non-existent-device-tag"))
                .thenThrow(new NotFoundException("[User] id: non-existent-device-tag not found"));

        // when, then
        assertThatThrownBy(() -> userService.getById("non-existent-device-tag"))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("[User] id: non-existent-device-tag not found");
        verify(repository).findByIdOrThrow("non-existent-device-tag");
    }

    @Test
    void 사용자를_저장하면_저장된_사용자를_반환한다() {
        // given
        when(repository.save(user)).thenReturn(user);

        // when
        User result = userService.save(user);

        // then
        assertThat(result).isEqualTo(user);
        verify(repository).save(user);
    }

    @Test
    void 사용자를_삭제한다() {
        // when
        userService.delete(user);

        // then
        verify(repository).delete(user);
    }

    @Test
    void 현재_디바이스태그를_가져온다() {
        // given
        try (MockedStatic<AuthUtils> mockedAuthUtils = mockStatic(AuthUtils.class)) {
            mockedAuthUtils.when(AuthUtils::getAuthenticationInfoDeviceTag).thenReturn("test-device-tag");

            // when
            String deviceTag = userService.getCurrentDeviceTag();

            // then
            assertThat(deviceTag).isEqualTo("test-device-tag");
            mockedAuthUtils.verify(AuthUtils::getAuthenticationInfoDeviceTag);
        }
    }

    @Test
    void 현재_사용자를_가져온다() {
        // given
        try (MockedStatic<AuthUtils> mockedAuthUtils = mockStatic(AuthUtils.class)) {
            mockedAuthUtils.when(AuthUtils::getAuthenticationInfoDeviceTag).thenReturn("test-device-tag");
            when(repository.findByIdOrThrow("test-device-tag")).thenReturn(user);

            // when
            User result = userService.getCurrentUser();

            // then
            assertThat(result).isEqualTo(user);
            mockedAuthUtils.verify(AuthUtils::getAuthenticationInfoDeviceTag);
            verify(repository).findByIdOrThrow("test-device-tag");
        }
    }

    @Test
    void 계정이_잠겨있으면_예외를_던진다() {
        // given
        when(repository.findById("test-device-tag")).thenReturn(Optional.of(user));
        user.lockAccount();

        // when, then
        assertThatThrownBy(() -> userService.handleAccountLock("test-device-tag"))
                .isInstanceOf(AccountLockedException.class)
                .hasMessage("Account is locked due to too many failed login attempts.");
        verify(repository).findById("test-device-tag");
    }

    @Test
    void 로그인_실패_횟수가_최대치를_넘으면_계정을_잠근다() {
        // given
        when(repository.findById("test-device-tag")).thenReturn(Optional.of(user));

        // when
        for (int i = 0; i < maxFailedAttempts; i++) {
            userService.handleFailedLogin("test-device-tag");
        }

        // then
        assertThat(user.isAccountLocked()).isTrue();
        verify(repository, times(maxFailedAttempts)).findById("test-device-tag");
        verify(repository, times(maxFailedAttempts)).save(user);
    }

    @Test
    void 로그인_실패_횟수를_초기화한다() {
        // given
        when(repository.findById("test-device-tag")).thenReturn(Optional.of(user));
        user.incrementFailedLoginAttempts();

        // when
        userService.resetFailedAttempts("test-device-tag");

        // then
        assertThat(user.getFailedLoginAttempts()).isZero();
        verify(repository).findById("test-device-tag");
        verify(repository).save(user);
    }

    @Test
    void 존재하지_않는_사용자를_조회하면_예외를_던진다() {
        // given
        when(repository.findById("non-existent-device-tag")).thenReturn(Optional.empty());

        // when, then
        assertThatThrownBy(() -> userService.handleAccountLock("non-existent-device-tag"))
                .isInstanceOf(BadCredentialsException.class)
                .hasMessage("[User] id: non-existent-device-tag not found");
        verify(repository).findById("non-existent-device-tag");
    }
}
