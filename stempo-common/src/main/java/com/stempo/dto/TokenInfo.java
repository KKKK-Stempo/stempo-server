package com.stempo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class TokenInfo {

    @Schema(description = "액세스 토큰", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJzdGVtcG8iLCJpYXQiOjE2MzIwNjQwMzMsImV4cCI6MTYzMjA2NzYzM30.1")
    private String accessToken;

    @Schema(description = "리프레시 토큰", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJzdGVtcG8iLCJpYXQiOjE2MzIwNjQwMzMsImV4cCI6MTYzMjA2NzYzM30.1")
    private String refreshToken;

    public static TokenInfo create(String accessToken, String refreshToken) {
        return TokenInfo.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }
}
