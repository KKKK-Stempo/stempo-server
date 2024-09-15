package com.stempo.api.domain.application.service;

import com.stempo.api.domain.presentation.dto.request.AuthRequestDto;
import com.stempo.api.domain.presentation.dto.response.TokenInfo;
import jakarta.servlet.http.HttpServletRequest;

public interface AuthService {

    String registerUser(AuthRequestDto requestDto);

    TokenInfo login(AuthRequestDto requestDto);

    TokenInfo reissueToken(HttpServletRequest request);
}
