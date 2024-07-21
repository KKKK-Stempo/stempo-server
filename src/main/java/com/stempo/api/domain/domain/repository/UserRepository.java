package com.stempo.api.domain.domain.repository;

import com.stempo.api.domain.domain.model.User;

import java.util.Optional;

public interface UserRepository {

    User save(User user);

    Optional<User> findById(String id);

    boolean existsById(String id);
}
