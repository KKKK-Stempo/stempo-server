package com.stempo.service;

import com.stempo.dto.TokenInfo;
import com.stempo.dto.request.AuthRequestDto;
import com.stempo.dto.request.TwoFactorAuthenticationRequestDto;
import jakarta.servlet.http.HttpServletRequest;

public interface AuthService {

    TokenInfo registerUser(AuthRequestDto requestDto);

    String unregisterUser();

    Object login(AuthRequestDto requestDto);

    TokenInfo reissueToken(HttpServletRequest request);

    TokenInfo authenticate(TwoFactorAuthenticationRequestDto requestDto);

    String resetAuthenticator(String deviceTag);
}
