package com.stempo.service;

import com.stempo.exception.AccountLockedException;
import com.stempo.model.User;
import com.stempo.repository.UserRepository;
import com.stempo.util.AuthUtils;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository repository;

    @Value("${security.max-failed-attempts:5}")
    private int maxFailedAttempts;

    @Override
    public Optional<User> findById(String id) {
        return repository.findById(id);
    }

    @Override
    public boolean existsById(String deviceTag) {
        return repository.existsById(deviceTag);
    }

    @Override
    public User getById(String deviceTag) {
        return repository.findByIdOrThrow(deviceTag);
    }

    @Override
    public User save(User user) {
        return repository.save(user);
    }

    @Override
    public void delete(User user) {
        repository.delete(user);
    }

    @Override
    public String getCurrentDeviceTag() {
        return AuthUtils.getAuthenticationInfoDeviceTag();
    }

    @Override
    public User getCurrentUser() {
        String deviceTag = getCurrentDeviceTag();
        return repository.findByIdOrThrow(deviceTag);
    }

    @Override
    @Transactional
    public void handleAccountLock(String deviceTag) {
        User user = getUserById(deviceTag);
        if (user.isAccountLocked()) {
            throw new AccountLockedException("Account is locked due to too many failed login attempts.");
        }
    }

    @Override
    @Transactional
    public void handleFailedLogin(String deviceTag) {
        User user = getUserById(deviceTag);
        user.incrementFailedLoginAttempts();
        if (user.getFailedLoginAttempts() >= maxFailedAttempts) {
            user.lockAccount();
            log.warn("User {} account locked after {} failed login attempts", user.getDeviceTag(), maxFailedAttempts);
        }
        save(user);
    }

    @Override
    @Transactional
    public void resetFailedAttempts(String deviceTag) {
        User user = getUserById(deviceTag);
        user.resetFailedLoginAttempts();
        save(user);
    }

    private User getUserById(String deviceTag) {
        return findById(deviceTag)
                .orElseThrow(() -> new BadCredentialsException("[User] id: " + deviceTag + " not found"));
    }
}
