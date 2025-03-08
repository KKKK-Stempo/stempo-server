package com.stempo.service;


import com.stempo.model.User;
import java.util.Optional;

public interface UserService {

    String encryptDeviceTag(String deviceTag);

    Optional<User> findById(String id);

    boolean existsById(String deviceTag);

    User getById(String deviceTag);

    User save(User user);

    void delete(User user);

    User getCurrentUser();

    void handleAccountLock(String deviceTag);

    void handleFailedLogin(String deviceTag);

    void resetFailedAttempts(String deviceTag);
}
