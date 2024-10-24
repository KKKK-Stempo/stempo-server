package com.stempo.service;

import com.stempo.application.JwtTokenService;
import com.stempo.dto.TokenInfo;
import com.stempo.dto.request.AuthRequestDto;

public class UserRegistrationCommand implements Command<TokenInfo> {

    private final UserRegistrationService userRegistrationService;
    private final AuthRequestDto requestDto;
    private final JwtTokenService jwtTokenService;

    public UserRegistrationCommand(UserRegistrationService userRegistrationService, AuthRequestDto requestDto,
            JwtTokenService jwtTokenService) {
        this.userRegistrationService = userRegistrationService;
        this.requestDto = requestDto;
        this.jwtTokenService = jwtTokenService;
    }

    @Override
    public TokenInfo execute() {
        return userRegistrationService.registerUser(requestDto, jwtTokenService);
    }
}
