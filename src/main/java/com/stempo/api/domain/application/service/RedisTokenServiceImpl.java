package com.stempo.api.domain.application.service;

import com.stempo.api.domain.domain.model.RedisToken;
import com.stempo.api.domain.domain.model.Role;
import com.stempo.api.domain.domain.repository.RedisTokenRepository;
import com.stempo.api.domain.presentation.dto.response.TokenInfo;
import com.stempo.api.global.auth.exception.TokenNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RedisTokenServiceImpl implements RedisTokenService {

    private final RedisTokenRepository redisTokenRepository;

    @Override
    public RedisToken findByAccessToken(String token) {
        return redisTokenRepository.findByAccessToken(token)
                .orElseThrow(() -> new TokenNotFoundException("존재하지 않는 토큰입니다."));
    }

    @Override
    public RedisToken findByRefreshToken(String token) {
        return redisTokenRepository.findByRefreshToken(token)
                .orElseThrow(() -> new TokenNotFoundException("존재하지 않는 토큰입니다."));
    }

    @Override
    public void saveToken(String id, Role role, TokenInfo tokenInfo) {
        RedisToken redisToken = RedisToken.create(id, role, tokenInfo);
        redisTokenRepository.save(redisToken);
    }
}
