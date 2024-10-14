package com.stempo.service;

import com.stempo.model.User;
import com.stempo.repository.UserRepository;
import com.stempo.util.AuthUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository repository;

    @Override
    public Optional<User> findById(String id) {
        return repository.findById(id);
    }

    @Override
    public boolean existsById(String deviceTag) {
        return repository.existsById(deviceTag);
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
        return AuthUtil.getAuthenticationInfoDeviceTag();
    }

    @Override
    public User getCurrentUser() {
        String deviceTag = getCurrentDeviceTag();
        return repository.findByIdOrThrow(deviceTag);
    }
}
