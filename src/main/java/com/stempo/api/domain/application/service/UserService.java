package com.stempo.api.domain.application.service;

import com.stempo.api.domain.domain.model.User;

import java.util.Optional;

public interface UserService {

    User registerUser(String deviceTag, String password);

    Optional<User> findById(String id);

    boolean existsById(String id);

    String getCurrentDeviceTag();

    User getCurrentUser();
}
