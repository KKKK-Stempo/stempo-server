package com.stempo.config;

import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;

public class CustomAuthenticationProvider implements AuthenticationProvider {

    private final UserDetailsService userDetailsService;
    private final PasswordEncoder passwordEncoder;

    public CustomAuthenticationProvider(UserDetailsService userDetailsService, PasswordEncoder passwordEncoder) {
        this.userDetailsService = userDetailsService;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String deviceTag = authentication.getName();
        String password = (String) authentication.getCredentials();

        UserDetails user = userDetailsService.loadUserByUsername(deviceTag);

        // 사용자 정보가 없을 경우 예외 발생
        if (user == null) {
            throw new BadCredentialsException("자격 증명에 실패하였습니다.");
        }

        // 비밀번호가 없는 계정인 경우, 입력된 비밀번호도 null 또는 빈 문자열인지 확인
        if (user.getPassword() == null || user.getPassword().isEmpty()) {
            if (password == null || password.isEmpty()) {
                return new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
            } else {
                throw new BadCredentialsException("자격 증명에 실패하였습니다.");
            }
        }

        // 비밀번호가 존재하는 경우 비밀번호를 비교 (BCryptPasswordEncoder를 사용하여 비교)
        if (password != null && passwordEncoder.matches(password, user.getPassword())) {
            return new UsernamePasswordAuthenticationToken(user, password, user.getAuthorities());
        }
        throw new BadCredentialsException("자격 증명에 실패하였습니다.");
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
