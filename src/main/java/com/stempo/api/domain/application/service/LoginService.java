package com.stempo.api.domain.application.service;

import com.stempo.api.domain.presentation.dto.response.TokenInfo;
import jakarta.servlet.http.HttpServletRequest;

public interface LoginService {

    TokenInfo loginOrRegister(String deviceTag, String password);

    TokenInfo reissueToken(HttpServletRequest request);
}
