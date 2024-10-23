package com.stempo.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.stempo.entity.UserEntity;
import com.stempo.exception.NotFoundException;
import com.stempo.mapper.UserMapper;
import com.stempo.model.Role;
import com.stempo.model.User;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class UserRepositoryImplTest {

    @Mock
    private UserJpaRepository userJpaRepository;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserRepositoryImpl userRepository;

    private User user;
    private UserEntity userEntity;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        user = User.builder()
                .deviceTag("device123")
                .password("password")
                .failedLoginAttempts(0)
                .accountLocked(false)
                .role(Role.USER)
                .build();

        userEntity = UserEntity.builder()
                .deviceTag("device123")
                .password("password")
                .failedLoginAttempts(0)
                .accountLocked(false)
                .role(Role.USER)
                .build();
    }

    @Test
    void 유저를_저장하면_성공한다() {
        // given
        when(userMapper.toEntity(any(User.class))).thenReturn(userEntity);
        when(userJpaRepository.save(any(UserEntity.class))).thenReturn(userEntity);
        when(userMapper.toDomain(any(UserEntity.class))).thenReturn(user);

        // when
        User savedUser = userRepository.save(user);

        // then
        assertThat(savedUser).isNotNull();
        assertThat(savedUser.getDeviceTag()).isEqualTo(user.getDeviceTag());
        verify(userJpaRepository, times(1)).save(userEntity);
    }

    @Test
    void 아이디로_유저를_조회하면_성공한다() {
        // given
        when(userJpaRepository.findById("device123")).thenReturn(Optional.of(userEntity));
        when(userMapper.toDomain(any(UserEntity.class))).thenReturn(user);

        // when
        Optional<User> result = userRepository.findById("device123");

        // then
        assertThat(result).isPresent();
        assertThat(result.get().getDeviceTag()).isEqualTo("device123");
    }

    @Test
    void 아이디로_유저를_조회하면_없을때_예외를_발생시킨다() {
        // given
        when(userJpaRepository.findById("device123")).thenReturn(Optional.empty());

        // when, then
        assertThrows(NotFoundException.class, () -> userRepository.findByIdOrThrow("device123"));
    }

    @Test
    void 유저가_존재하는지_확인하면_성공한다() {
        // given
        when(userJpaRepository.existsById("device123")).thenReturn(true);

        // when
        boolean exists = userRepository.existsById("device123");

        // then
        assertThat(exists).isTrue();
    }

    @Test
    void 유저를_삭제하면_성공한다() {
        // given
        when(userMapper.toEntity(any(User.class))).thenReturn(userEntity);

        // when
        userRepository.delete(user);

        // then
        verify(userJpaRepository, times(1)).delete(userEntity);
    }
}
