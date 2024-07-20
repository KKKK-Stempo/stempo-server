package com.stempo.api.domain.application.service;

import com.stempo.api.domain.presentation.dto.request.LoginRequestDto;
import com.stempo.api.domain.presentation.dto.response.TokenInfo;

public interface LoginService {
    TokenInfo login(LoginRequestDto requestDto);
}
