package com.stempo.api.domain.persistence.repository;

import com.stempo.api.domain.domain.model.User;
import com.stempo.api.domain.domain.repository.UserRepository;
import com.stempo.api.domain.persistence.entity.UserEntity;
import com.stempo.api.domain.persistence.mappper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepository {

    private final UserJpaRepository repository;

    @Override
    public User save(User user) {
        UserEntity entity = repository.save(UserMapper.toEntity(user));
        return UserMapper.toDomain(entity);
    }

    @Override
    public Optional<User> findById(String id) {
        return repository.findById(id)
                .map(UserMapper::toDomain);
    }

    @Override
    public boolean existsById(String id) {
        return repository.existsById(id);
    }
}
