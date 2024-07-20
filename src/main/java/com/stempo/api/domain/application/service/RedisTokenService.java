package com.stempo.api.domain.application.service;

import com.stempo.api.domain.domain.model.RedisToken;
import com.stempo.api.domain.domain.model.Role;
import com.stempo.api.domain.presentation.dto.response.TokenInfo;

public interface RedisTokenService {

    RedisToken findByAccessToken(String token);

    RedisToken findByRefreshToken(String token);

    void saveToken(String id, Role role, TokenInfo tokenInfo);
}
