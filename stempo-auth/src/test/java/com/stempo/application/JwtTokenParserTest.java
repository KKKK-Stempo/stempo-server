package com.stempo.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import com.stempo.dto.TokenInfo;
import com.stempo.exception.TokenValidateException;
import com.stempo.model.Role;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import java.security.Key;
import java.util.Collection;
import java.util.Date;
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
class JwtTokenParserTest {

    private JwtTokenParser jwtTokenParser;
    private JwtTokenGenerator jwtTokenGenerator;

    @Mock
    private HttpServletRequest request;

    @Mock
    private Authentication authentication;

    @Mock
    private UserDetails userDetails;

    private Key key;

    @BeforeEach
    void setUp() {
        String secretKey = "supersecretkeythatmustbe32byteslong!";
        key = Keys.hmacShaKeyFor(secretKey.getBytes());

        long accessTokenDuration = 60000L; // 1분
        long refreshTokenDuration = 120000L; // 2분

        jwtTokenGenerator = new JwtTokenGenerator(secretKey, accessTokenDuration, refreshTokenDuration);
        jwtTokenParser = new JwtTokenParser(secretKey);
    }

    @Test
    void 토큰에서_권한이_있는_사용자를_인증한다() {
        // given
        String username = "testUser";
        Collection<GrantedAuthority> authorities = List.of((GrantedAuthority) Role.USER::name);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userDetails.getUsername()).thenReturn(username);
        when(userDetails.getAuthorities()).thenReturn((Collection) authorities);

        TokenInfo tokenInfo = jwtTokenGenerator.generateToken(authentication);

        // when
        Authentication parsedAuthentication = jwtTokenParser.getAuthentication(tokenInfo.getAccessToken());

        // then
        assertThat(parsedAuthentication).isNotNull();
        assertThat(parsedAuthentication.getName()).isEqualTo(username);
        assertThat(parsedAuthentication.getAuthorities()).hasSize(1);
        assertThat(parsedAuthentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals(Role.USER.getKey()))).isTrue();
    }

    @Test
    void 권한이_없는_토큰_인증_실패시_예외를_던진다() {
        // given
        String username = "testUser";

        // JWT 토큰 생성 시 role 필드를 포함하지 않음
        String tokenWithoutRole = Jwts.builder()
                .subject(username)
                .signWith(key)
                .compact();

        // when, then
        assertThatThrownBy(() -> jwtTokenParser.getAuthentication(tokenWithoutRole))
                .isInstanceOf(TokenValidateException.class)
                .hasMessage("권한 정보가 없는 토큰입니다.");
    }

    @Test
    void 요청_헤더에서_정상적으로_토큰을_추출한다() {
        // given
        String bearerToken = "Bearer testToken";
        when(request.getHeader("Authorization")).thenReturn(bearerToken);

        // when
        String token = jwtTokenParser.resolveToken(request);

        // then
        assertThat(token).isEqualTo("testToken");
    }

    @Test
    void 요청_헤더에_토큰이_없으면_null을_반환한다() {
        // given
        when(request.getHeader("Authorization")).thenReturn(null);

        // when
        String token = jwtTokenParser.resolveToken(request);

        // then
        assertThat(token).isNull();
    }

    @Test
    void 정상적으로_Claims를_파싱한다() {
        // given
        String username = "testUser";
        Collection<? extends GrantedAuthority> authorities = List.of(Role.USER::name);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userDetails.getUsername()).thenReturn(username);
        when(userDetails.getAuthorities()).thenReturn((Collection) authorities);

        // JWT 토큰 생성
        TokenInfo tokenInfo = jwtTokenGenerator.generateToken(authentication);

        // when
        Claims claims = jwtTokenParser.parseClaims(tokenInfo.getAccessToken());

        // then
        assertThat(claims.getSubject()).isEqualTo(username);
        assertThat(claims.get("role")).isEqualTo(Role.USER.name());
    }

    @Test
    void 만료된_토큰을_파싱하면_예외를_던진다() {
        // given
        String expiredToken = Jwts.builder()
                .subject("testUser")
                .expiration(new Date(System.currentTimeMillis() - 1000))
                .signWith(key)
                .compact();

        // when, then
        assertThatThrownBy(() -> jwtTokenParser.parseClaims(expiredToken))
                .isInstanceOf(TokenValidateException.class)
                .hasMessage("만료된 토큰입니다.");
    }
}
