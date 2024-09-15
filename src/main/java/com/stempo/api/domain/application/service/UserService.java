package com.stempo.api.domain.application.service;

import com.stempo.api.domain.domain.model.User;
import com.stempo.api.domain.presentation.dto.request.UserRequestDto;

import java.util.Optional;

public interface UserService {

    String registerUser(UserRequestDto requestDto);

    Optional<User> findById(String id);

    String getCurrentDeviceTag();

    User getCurrentUser();
}
