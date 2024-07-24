package com.stempo.api.domain.persistence.repository;

import com.stempo.api.domain.domain.model.User;
import com.stempo.api.domain.domain.repository.UserRepository;
import com.stempo.api.domain.persistence.entity.UserEntity;
import com.stempo.api.domain.persistence.mappper.UserMapper;
import com.stempo.api.global.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepository {

    private final UserJpaRepository repository;
    private final UserMapper mapper;

    @Override
    public User save(User user) {
        UserEntity jpaEntity = mapper.toEntity(user);
        UserEntity savedEntity = repository.save(jpaEntity);
        return mapper.toDomain(savedEntity);
    }

    @Override
    public Optional<User> findById(String id) {
        return repository.findById(id)
                .map(mapper::toDomain);
    }

    @Override
    public User findByIdOrThrow(String deviceTag) {
        return repository.findById(deviceTag)
                .map(mapper::toDomain)
                .orElseThrow(() -> new NotFoundException("[User] id: " + deviceTag + " not found"));
    }

    @Override
    public boolean existsById(String id) {
        return repository.existsById(id);
    }
}
