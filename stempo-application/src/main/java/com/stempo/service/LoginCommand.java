package com.stempo.service;

import com.stempo.application.JwtTokenService;
import com.stempo.dto.request.AuthRequestDto;

public class LoginCommand implements Command<Object> {

    private final AuthenticationService authenticationService;
    private final AuthRequestDto requestDto;
    private final JwtTokenService jwtTokenService;
    private final TotpAuthenticatorService totpAuthenticatorService;

    public LoginCommand(AuthenticationService authenticationService, AuthRequestDto requestDto,
            JwtTokenService jwtTokenService, TotpAuthenticatorService totpAuthenticatorService) {
        this.authenticationService = authenticationService;
        this.requestDto = requestDto;
        this.jwtTokenService = jwtTokenService;
        this.totpAuthenticatorService = totpAuthenticatorService;
    }

    @Override
    public Object execute() {
        return authenticationService.login(requestDto, jwtTokenService, totpAuthenticatorService);
    }
}
