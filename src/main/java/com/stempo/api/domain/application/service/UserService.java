package com.stempo.api.domain.application.service;

import com.stempo.api.domain.domain.model.User;

import java.util.Optional;

public interface UserService {

    Optional<User> findById(String id);

    boolean existsById(String deviceTag);

    User save(User user);

    String getCurrentDeviceTag();

    User getCurrentUser();
}
