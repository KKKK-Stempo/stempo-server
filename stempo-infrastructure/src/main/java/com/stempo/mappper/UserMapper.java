package com.stempo.mappper;

import com.stempo.entity.UserEntity;
import com.stempo.model.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public UserEntity toEntity(User user) {
        return UserEntity.builder()
                .deviceTag(user.getDeviceTag())
                .password(user.getPassword())
                .role(user.getRole())
                .build();
    }

    public User toDomain(UserEntity entity) {
        return User.builder()
                .deviceTag(entity.getDeviceTag())
                .password(entity.getPassword())
                .role(entity.getRole())
                .build();
    }
}
