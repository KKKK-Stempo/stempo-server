package com.stempo.api.domain.application.service;

import com.stempo.api.domain.domain.model.User;
import com.stempo.api.domain.domain.repository.UserRepository;
import com.stempo.api.domain.presentation.dto.request.UserRegisterRequestDto;
import com.stempo.api.global.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordService passwordService;

    @Override
    public String registerUser(UserRegisterRequestDto requestDto) {
        User user = UserRegisterRequestDto.toDomain(requestDto);
        String encodedPassword = passwordService.encodePassword(user.getPassword());
        user.updatePassword(encodedPassword);
        return userRepository.save(user).getId();
    }

    @Override
    public Optional<User> findById(String id) {
        return userRepository.findById(id);
    }

    @Override
    public User findByIdOrThrow(String id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("[User] id: " + id + " not found"));
    }
}
