package com.stempo.api.domain.persistence.repository;

import com.stempo.api.domain.domain.model.User;
import com.stempo.api.domain.domain.repository.UserRepository;
import com.stempo.api.domain.persistence.entity.UserEntity;
import com.stempo.api.domain.persistence.mappper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@RequiredArgsConstructor
@Repository
public class UserRepositoryImpl implements UserRepository {

    private final UserJpaRepository userJpaRepository;

    @Override
    public User save(User user) {
        UserEntity entity = userJpaRepository.save(UserMapper.toEntity(user));
        return UserMapper.toDomain(entity);
    }
}
