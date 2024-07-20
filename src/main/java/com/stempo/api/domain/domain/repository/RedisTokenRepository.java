package com.stempo.api.domain.domain.repository;

import com.stempo.api.domain.domain.model.RedisToken;

import java.util.Optional;

public interface RedisTokenRepository {

    Optional<RedisToken> findByAccessToken(String token);

    Optional<RedisToken> findByRefreshToken(String token);

    void save(RedisToken redisToken);
}
