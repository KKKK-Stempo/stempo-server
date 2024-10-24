package com.stempo.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import com.stempo.dto.TokenInfo;
import com.stempo.model.Role;
import io.jsonwebtoken.Claims;
import java.util.Collection;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@ExtendWith(MockitoExtension.class)
class JwtTokenGeneratorTest {

    private final long accessTokenDuration = 60000L; // 1분
    private final long refreshTokenDuration = 120000L; // 2분

    private JwtTokenGenerator jwtTokenGenerator;
    private JwtTokenParser jwtTokenParser;

    @Mock
    private Authentication authentication;

    @Mock
    private UserDetails userDetails;

    @BeforeEach
    void setUp() {
        String secretKey = "supersecretkeythatmustbe32byteslong!";
        jwtTokenGenerator = new JwtTokenGenerator(secretKey, accessTokenDuration, refreshTokenDuration);
        jwtTokenParser = new JwtTokenParser(secretKey);
    }

    @Test
    void 정상적으로_토큰을_생성한다() {
        // given
        String username = "testUser";
        Collection<GrantedAuthority> authorities = List.of((GrantedAuthority) Role.USER::name);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userDetails.getUsername()).thenReturn(username);
        when(userDetails.getAuthorities()).thenReturn((Collection) authorities);

        // when
        TokenInfo tokenInfo = jwtTokenGenerator.generateToken(authentication);

        // then
        assertThat(tokenInfo).isNotNull();
        assertThat(tokenInfo.getAccessToken()).isNotNull();
        assertThat(tokenInfo.getRefreshToken()).isNotNull();

        // 토큰의 구조 검증
        Claims claims = jwtTokenParser.parseClaims(tokenInfo.getAccessToken());
        assertThat(claims.getSubject()).isEqualTo(username);
        assertThat(claims.get("role")).isEqualTo(Role.USER.name());
        assertThat(claims.getIssuedAt()).isNotNull();
        assertThat(claims.getExpiration().getTime())
                .isEqualTo(claims.getIssuedAt().getTime() + accessTokenDuration);

        claims = jwtTokenParser.parseClaims(tokenInfo.getRefreshToken());
        assertThat(claims.getSubject()).isEqualTo(username);
        assertThat(claims.get("role")).isEqualTo(Role.USER.name());
        assertThat(claims.getIssuedAt()).isNotNull();
        assertThat(claims.getExpiration().getTime())
                .isEqualTo(claims.getIssuedAt().getTime() + refreshTokenDuration);
    }
}
