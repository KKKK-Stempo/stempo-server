package com.stempo.api.domain.application.service;

import com.stempo.api.domain.application.exception.UserAlreadyExistsException;
import com.stempo.api.domain.domain.model.User;
import com.stempo.api.domain.domain.repository.UserRepository;
import com.stempo.api.domain.presentation.dto.request.UserRequestDto;
import com.stempo.api.global.auth.util.AuthUtil;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public String registerUser(UserRequestDto requestDto) {
        String deviceTag = requestDto.getDeviceTag();
        String password = requestDto.getPassword();

        if (repository.existsById(deviceTag)) {
            throw new UserAlreadyExistsException("User already exists.");
        }

        String finalPassword = StringUtils.isEmpty(password) ? null : passwordEncoder.encode(password);
        User user = User.create(deviceTag, finalPassword);
        return repository.save(user).getDeviceTag();
    }


    @Override
    public Optional<User> findById(String id) {
        return repository.findById(id);
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
