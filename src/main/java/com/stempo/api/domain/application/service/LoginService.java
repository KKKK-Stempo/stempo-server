package com.stempo.api.domain.application.service;

import com.stempo.api.domain.presentation.dto.request.LoginRequestDto;
import com.stempo.api.domain.presentation.dto.response.TokenInfo;
import jakarta.servlet.http.HttpServletRequest;

public interface LoginService {

    TokenInfo login(LoginRequestDto requestDto);

    TokenInfo reissueToken(HttpServletRequest request);
}
