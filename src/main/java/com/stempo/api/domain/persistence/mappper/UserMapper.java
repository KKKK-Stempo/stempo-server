package com.stempo.api.domain.persistence.mappper;

import com.stempo.api.domain.domain.model.User;
import com.stempo.api.domain.persistence.entity.UserEntity;
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
