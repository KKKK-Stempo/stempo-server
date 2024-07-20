package com.stempo.api.domain.application.service;

import com.stempo.api.domain.presentation.dto.request.UserRegisterRequestDto;

public interface UserService {
    String registerUser(UserRegisterRequestDto request);
}
