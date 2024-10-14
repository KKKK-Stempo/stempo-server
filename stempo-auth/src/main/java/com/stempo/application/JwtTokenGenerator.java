package com.stempo.application;

import com.stempo.dto.TokenInfo;
import com.stempo.model.Role;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Collection;
import java.util.Date;

@Component
public class JwtTokenGenerator {

    private final Key key;
    private final long accessTokenDuration;
    private final long refreshTokenDuration;

    public JwtTokenGenerator(
            @Value("${security.jwt.secret-key}") String secretKey,
            @Value("${security.jwt.token-validity-in-seconds.access-token}") long accessTokenDuration,
            @Value("${security.jwt.token-validity-in-seconds.refresh-token}") long refreshTokenDuration
    ) {
        this.key = Keys.hmacShaKeyFor(secretKey.getBytes());
        this.accessTokenDuration = accessTokenDuration;
        this.refreshTokenDuration = refreshTokenDuration;
    }

    public TokenInfo generateToken(Authentication authentication) {
        UserDetails principal = (UserDetails) authentication.getPrincipal();
        Collection<? extends GrantedAuthority> authorities = principal.getAuthorities();

        String id = principal.getUsername();
        Role role = Role.valueOf(authorities.stream().findFirst().isPresent() ? authorities.stream().findFirst().get().getAuthority() : "ROLE_USER");
        return generateToken(id, role);
    }

    public TokenInfo generateToken(String id, Role role) {
        Date expiry = new Date();
        Date accessTokenExpiry = new Date(expiry.getTime() + (accessTokenDuration));
        String accessToken = Jwts.builder()
                .subject(id)
                .claim("role", role)
                .issuedAt(expiry)
                .expiration(accessTokenExpiry)
                .signWith(key)
                .compact();

        Date refreshTokenExpiry = new Date(expiry.getTime() + (refreshTokenDuration));
        String refreshToken = Jwts.builder()
                .subject(id)
                .claim("role", role)
                .issuedAt(expiry)
                .expiration(refreshTokenExpiry)
                .signWith(key)
                .compact();

        return TokenInfo.create(accessToken, refreshToken);
    }
}
