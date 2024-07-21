package com.stempo.api.domain.persistence.repository;

import com.stempo.api.domain.domain.model.RedisToken;
import com.stempo.api.domain.domain.repository.RedisTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class RedisTokenRepositoryImpl implements RedisTokenRepository {

    private final RedisTokenJpaRepository repository;

    @Override
    public Optional<RedisToken> findByAccessToken(String token) {
        return repository.findByAccessToken(token);
    }

    @Override
    public Optional<RedisToken> findByRefreshToken(String token) {
        return repository.findByRefreshToken(token);
    }

    @Override
    public void save(RedisToken redisToken) {
        repository.save(redisToken);
    }
}
