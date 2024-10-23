package com.stempo.mapper;

import com.stempo.entity.UserEntity;
import com.stempo.model.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public UserEntity toEntity(User user) {
        return UserEntity.builder()
                .deviceTag(user.getDeviceTag())
                .password(user.getPassword())
                .failedLoginAttempts(user.getFailedLoginAttempts())
                .accountLocked(user.isAccountLocked())
                .role(user.getRole())
                .build();
    }

    public User toDomain(UserEntity entity) {
        return User.builder()
                .deviceTag(entity.getDeviceTag())
                .password(entity.getPassword())
                .failedLoginAttempts(entity.getFailedLoginAttempts())
                .accountLocked(entity.isAccountLocked())
                .role(entity.getRole())
                .build();
    }
}
