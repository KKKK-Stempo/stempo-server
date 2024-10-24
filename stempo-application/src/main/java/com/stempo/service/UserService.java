package com.stempo.service;


import com.stempo.model.User;
import java.util.Optional;

public interface UserService {

    Optional<User> findById(String id);

    boolean existsById(String deviceTag);

    User getById(String deviceTag);

    User save(User user);

    void delete(User user);

    String getCurrentDeviceTag();

    User getCurrentUser();

    void handleAccountLock(String deviceTag);

    void handleFailedLogin(String deviceTag);

    void resetFailedAttempts(String deviceTag);
}
