package com.stempo.service;

import com.stempo.dto.TokenInfo;
import com.stempo.dto.request.AuthRequestDto;
import jakarta.servlet.http.HttpServletRequest;

public interface AuthService {

    TokenInfo registerUser(AuthRequestDto requestDto);

    String unregisterUser();

    TokenInfo login(AuthRequestDto requestDto);

    TokenInfo reissueToken(HttpServletRequest request);
}
