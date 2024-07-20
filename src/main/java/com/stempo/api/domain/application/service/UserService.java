package com.stempo.api.domain.application.service;

import com.stempo.api.domain.domain.model.User;
import com.stempo.api.domain.presentation.dto.request.UserRegisterRequestDto;

import java.util.Optional;

public interface UserService {

    String registerUser(UserRegisterRequestDto request);

    Optional<User> findById(String id);

    User findByIdOrThrow(String id);

    boolean existsById(String id);
}
