package com.stempo.api.domain.application.service;

import com.stempo.api.domain.domain.model.User;
import com.stempo.api.domain.domain.repository.UserRepository;
import com.stempo.api.global.util.PasswordUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordService passwordService;
    private final PasswordUtil passwordUtil;

    @Override
    public User registerUser(String deviceTag, String password) {
        String rawPassword = password != null ? password : passwordUtil.generateStrongPassword();
        User user = User.create(deviceTag, rawPassword);
        String encodedPassword = passwordService.encodePassword(user.getPassword());
        user.updatePassword(encodedPassword);
        return userRepository.save(user);
    }

    @Override
    public Optional<User> findById(String id) {
        return userRepository.findById(id);
    }

    @Override
    public boolean existsById(String id) {
        return userRepository.existsById(id);
    }
}
