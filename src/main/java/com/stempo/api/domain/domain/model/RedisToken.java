package com.stempo.api.domain.domain.model;

import com.stempo.api.domain.presentation.dto.response.TokenInfo;
import jakarta.persistence.Column;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

@Getter
@Setter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@RedisHash(value = "refresh", timeToLive = 60 * 60 * 24 * 14)
public class RedisToken {

    @Id
    @Column(name = "user_id")
    private String id;

    private Role role;

    @Indexed
    private String accessToken;

    @Indexed
    private String refreshToken;

    public static RedisToken create(String id, Role role, TokenInfo tokenInfo) {
        return RedisToken.builder()
                .id(id)
                .role(role)
                .accessToken(tokenInfo.getAccessToken())
                .refreshToken(tokenInfo.getRefreshToken())
                .build();
    }
}
