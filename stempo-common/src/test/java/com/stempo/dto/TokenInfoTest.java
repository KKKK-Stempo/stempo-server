package com.stempo.dto;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class TokenInfoTest {

    @Test
    void 토큰정보가_정상적으로_생성되는지_확인한다() {
        // given
        String accessToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.accessToken";
        String refreshToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.refreshToken";

        // when
        TokenInfo tokenInfo = TokenInfo.create(accessToken, refreshToken);

        // then
        assertThat(tokenInfo).isNotNull();
        assertThat(tokenInfo.getAccessToken()).isEqualTo(accessToken);
        assertThat(tokenInfo.getRefreshToken()).isEqualTo(refreshToken);
    }
}
