package com.stempo.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.stempo.entity.UserEntity;
import com.stempo.model.Role;
import com.stempo.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class UserMapperTest {

    private UserMapper userMapper;

    @BeforeEach
    void setUp() {
        userMapper = new UserMapper();
    }

    @Test
    void 도메인을_엔티티로_매핑한다() {
        // given
        User user = User.builder()
                .deviceTag("device123")
                .password("password123")
                .failedLoginAttempts(3)
                .accountLocked(false)
                .role(Role.USER)
                .build();

        // when
        UserEntity entity = userMapper.toEntity(user);

        // then
        assertThat(entity.getDeviceTag()).isEqualTo(user.getDeviceTag());
        assertThat(entity.getPassword()).isEqualTo(user.getPassword());
        assertThat(entity.getFailedLoginAttempts()).isEqualTo(user.getFailedLoginAttempts());
        assertThat(entity.isAccountLocked()).isEqualTo(user.isAccountLocked());
        assertThat(entity.getRole()).isEqualTo(user.getRole());
    }

    @Test
    void 엔티티를_도메인으로_매핑한다() {
        // given
        UserEntity entity = UserEntity.builder()
                .deviceTag("device123")
                .password("password123")
                .failedLoginAttempts(3)
                .accountLocked(false)
                .role(Role.USER)
                .build();

        // when
        User user = userMapper.toDomain(entity);

        // then
        assertThat(user.getDeviceTag()).isEqualTo(entity.getDeviceTag());
        assertThat(user.getPassword()).isEqualTo(entity.getPassword());
        assertThat(user.getFailedLoginAttempts()).isEqualTo(entity.getFailedLoginAttempts());
        assertThat(user.isAccountLocked()).isEqualTo(entity.isAccountLocked());
        assertThat(user.getRole()).isEqualTo(entity.getRole());
    }
}
