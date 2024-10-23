package com.stempo.application;

import static org.assertj.core.api.Assertions.assertThat;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.security.Key;
import java.util.Date;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;

class JwtTokenValidatorTest {

    private JwtTokenValidator jwtTokenValidator;
    private Key key;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        String secretKey = "supersecretkeythatmustbe32byteslong!";
        key = Keys.hmacShaKeyFor(secretKey.getBytes());

        long refreshTokenDuration = 120000L; // 2분

        JwtTokenParser jwtTokenParser = new JwtTokenParser(secretKey);
        jwtTokenValidator = new JwtTokenValidator(secretKey, refreshTokenDuration, jwtTokenParser);
    }

    @Test
    void 유효한_토큰을_검증한다() {
        // given
        String validToken = Jwts.builder()
                .subject("testUser")
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + 60000)) // 1분 유효
                .signWith(key)
                .compact();

        // when
        boolean isValid = jwtTokenValidator.validateToken(validToken);

        // then
        assertThat(isValid).isTrue();
    }

    @Test
    void 만료된_토큰은_검증에_실패한다() {
        // given
        String expiredToken = Jwts.builder()
                .subject("testUser")
                .issuedAt(new Date(System.currentTimeMillis() - 60000))
                .expiration(new Date(System.currentTimeMillis() - 1000))
                .signWith(key)
                .compact();

        // when
        boolean isValid = jwtTokenValidator.validateToken(expiredToken);

        // then
        assertThat(isValid).isFalse();
    }

    @Test
    void 잘못된_서명을_가진_토큰은_검증에_실패한다() {
        // given
        Key otherKey = Keys.hmacShaKeyFor("anothersecretkeythatmustbe32byteslong!".getBytes());
        String invalidToken = Jwts.builder()
                .subject("testUser")
                .signWith(otherKey)
                .compact();

        // when
        boolean isValid = jwtTokenValidator.validateToken(invalidToken);

        // then
        assertThat(isValid).isFalse();
    }

    @Test
    void 비어있는_토큰은_검증에_실패한다() {
        // given
        String emptyToken = "";

        // when
        boolean isValid = jwtTokenValidator.validateToken(emptyToken);

        // then
        assertThat(isValid).isFalse();
    }

    @Test
    void 유효한_리프레시_토큰을_확인한다() {
        // given
        String refreshToken = Jwts.builder()
                .subject("testUser")
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + 120000)) // 2분 유효
                .signWith(key)
                .compact();

        // when
        boolean isRefreshToken = jwtTokenValidator.isRefreshToken(refreshToken);

        // then
        assertThat(isRefreshToken).isTrue();
    }

    @Test
    void 리프레시_토큰_아닌_경우를_확인한다() {
        // given
        String accessToken = Jwts.builder()
                .subject("testUser")
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + 60000)) // 1분 유효
                .signWith(key)
                .compact();

        // when
        boolean isRefreshToken = jwtTokenValidator.isRefreshToken(accessToken);

        // then
        assertThat(isRefreshToken).isFalse();
    }

    @Test
    void validateTokenSilently_유효한_토큰을_검증한다() {
        // given
        String validToken = Jwts.builder()
                .subject("testUser")
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + 60000)) // 1분 유효
                .signWith(key)
                .compact();

        // when
        boolean isValid = jwtTokenValidator.validateTokenSilently(validToken);

        // then
        assertThat(isValid).isTrue();
    }

    @Test
    void validateTokenSilently_잘못된_토큰은_검증에_실패한다() {
        // given
        Key otherKey = Keys.hmacShaKeyFor("anothersecretkeythatmustbe32byteslong!".getBytes());
        String invalidToken = Jwts.builder()
                .subject("testUser")
                .signWith(otherKey)
                .compact();

        // when
        boolean isValid = jwtTokenValidator.validateTokenSilently(invalidToken);

        // then
        assertThat(isValid).isFalse();
    }
}
