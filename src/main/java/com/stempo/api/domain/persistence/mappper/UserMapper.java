package com.stempo.api.domain.persistence.mappper;

import com.stempo.api.domain.domain.model.User;
import com.stempo.api.domain.persistence.entity.UserEntity;

public class UserMapper {

    public static UserEntity toEntity(User user) {
        return UserEntity.builder()
                .id(user.getId())
                .password(user.getPassword())
                .role(user.getRole())
                .build();
    }

    public static User toDomain(UserEntity entity) {
        return User.builder()
                .id(entity.getId())
                .password(entity.getPassword())
                .role(entity.getRole())
                .build();
    }
}
