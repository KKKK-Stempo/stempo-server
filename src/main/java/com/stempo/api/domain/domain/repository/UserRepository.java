package com.stempo.api.domain.domain.repository;

import com.stempo.api.domain.domain.model.User;

public interface UserRepository {
    User save(User user);
}
