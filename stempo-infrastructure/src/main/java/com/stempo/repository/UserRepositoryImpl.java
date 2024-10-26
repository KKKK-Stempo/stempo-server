package com.stempo.repository;

import com.stempo.entity.UserEntity;
import com.stempo.exception.BaseException;
import com.stempo.exception.ErrorCode;
import com.stempo.mapper.UserMapper;
import com.stempo.model.User;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepository {

    private final UserJpaRepository repository;
    private final UserMapper mapper;

    @Override
    public User save(User user) {
        UserEntity entity = mapper.toEntity(user);
        UserEntity savedEntity = repository.save(entity);
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
                .orElseThrow(() -> new BaseException(ErrorCode.RESOURCE_NOT_FOUND));
    }

    @Override
    public boolean existsById(String id) {
        return repository.existsById(id);
    }

    @Override
    public void delete(User user) {
        UserEntity entity = mapper.toEntity(user);
        repository.delete(entity);
    }
}
