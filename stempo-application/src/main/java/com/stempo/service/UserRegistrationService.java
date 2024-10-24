package com.stempo.service;

import com.stempo.application.JwtTokenService;
import com.stempo.config.AesConfig;
import com.stempo.dto.TokenInfo;
import com.stempo.dto.request.AuthRequestDto;
import com.stempo.exception.InvalidPasswordException;
import com.stempo.exception.UserAlreadyExistsException;
import com.stempo.model.User;
import com.stempo.util.EncryptionUtils;
import com.stempo.util.PasswordValidator;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserRegistrationService {

    private final UserService userService;
    private final PasswordValidator passwordValidator;
    private final PasswordEncoder passwordEncoder;
    private final EncryptionUtils encryptionUtils;
    private final AesConfig aesConfig;

    @Transactional
    public TokenInfo registerUser(AuthRequestDto requestDto, JwtTokenService tokenService) {
        String deviceTag = encryptDeviceTag(requestDto.getDeviceTag());
        String password = requestDto.getPassword();

        handleDuplicateUser(deviceTag);
        validatePassword(password, deviceTag);

        User user = createUser(deviceTag, password);
        userService.save(user);

        return tokenService.generateToken(user.getDeviceTag(), user.getRole());
    }

    private void handleDuplicateUser(String deviceTag) {
        if (userService.existsById(deviceTag)) {
            throw new UserAlreadyExistsException("User already exists.");
        }
    }

    private User createUser(String deviceTag, String password) {
        String encryptedPassword = StringUtils.isEmpty(password) ? null : encryptPassword(password);
        return User.create(deviceTag, encryptedPassword);
    }

    private void validatePassword(String password, String deviceTag) {
        if (!StringUtils.isEmpty(password) && !passwordValidator.isValid(password, deviceTag)) {
            throw new InvalidPasswordException("Password does not meet the required criteria.");
        }
    }

    private String encryptDeviceTag(String deviceTag) {
        return encryptionUtils.encryptWithHashedIV(deviceTag, aesConfig.getDeviceTagSecretKey());
    }

    private String encryptPassword(String password) {
        return passwordEncoder.encode(password);
    }
}

