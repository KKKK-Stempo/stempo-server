package com.stempo.service;

import com.stempo.application.JwtTokenService;
import com.stempo.dto.TokenInfo;
import com.stempo.dto.request.AuthRequestDto;
import com.stempo.dto.request.TwoFactorAuthenticationRequestDto;
import com.stempo.exception.InvalidPasswordException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {

    private final UserRegistrationService userRegistrationService;
    private final AuthenticationService authenticationService;
    private final TotpService totpService;
    private final TokenService tokenService;
    private final UserEventService userEventService;
    private final JwtTokenService jwtTokenService;
    private final TotpAuthenticatorService totpAuthenticatorService;

    @Override
    @Transactional
    public TokenInfo registerUser(AuthRequestDto requestDto) {
        return new UserRegistrationCommand(userRegistrationService, requestDto, jwtTokenService).execute();
    }

    @Override
    @Transactional
    public String unregisterUser() {
        return userEventService.unregisterUser();
    }

    @Override
    @Transactional(noRollbackFor = {BadCredentialsException.class, InvalidPasswordException.class})
    public Object login(AuthRequestDto requestDto) {
        return new LoginCommand(authenticationService, requestDto, jwtTokenService, totpAuthenticatorService).execute();
    }

    @Override
    @Transactional
    public TokenInfo reissueToken(HttpServletRequest request) {
        return tokenService.reissueToken(request);
    }

    @Override
    @Transactional(noRollbackFor = {BadCredentialsException.class})
    public TokenInfo authenticate(TwoFactorAuthenticationRequestDto requestDto) {
        return totpService.authenticate(requestDto, jwtTokenService);
    }

    @Override
    @Transactional
    public String resetAuthenticator(String deviceTag) {
        return totpService.resetAuthenticator(deviceTag);
    }
}
