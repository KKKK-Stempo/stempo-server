package com.stempo.repository;

import com.stempo.model.User;

import java.util.Optional;

public interface UserRepository {

    User save(User user);

    Optional<User> findById(String id);

    User findByIdOrThrow(String deviceTag);

    boolean existsById(String id);

    void delete(User user);
}
