package com.stempo.api.domain.persistence.repository;

import com.stempo.api.domain.domain.model.RedisToken;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface RedisTokenJpaRepository extends CrudRepository<RedisToken, String> {

    Optional<RedisToken> findByAccessToken(String token);

    Optional<RedisToken> findByRefreshToken(String token);
}
