package com.stempo.controller;

import com.stempo.annotation.SuccessApiResponse;
import com.stempo.dto.ApiResponse;
import com.stempo.dto.TokenInfo;
import com.stempo.dto.request.AuthRequestDto;
import com.stempo.dto.request.TwoFactorAuthenticationRequestDto;
import com.stempo.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Tag(name = "Auth", description = "가입/인증")
public class AuthController {

    private final AuthService authService;

    @Operation(summary = "회원 가입", description = "ROLE_ANONYMOUS 이상의 권한이 필요함")
    @PostMapping("/api/v1/auth/register")
    public ApiResponse<TokenInfo> registerUser(
            @Valid @RequestBody AuthRequestDto requestDto
    ) {
        TokenInfo token = authService.registerUser(requestDto);
        return ApiResponse.success(token);
    }

    @Operation(summary = "[U] 회원 탈퇴", description = "ROLE_USER 이상의 권한이 필요함")
    @SuccessApiResponse(data = "deviceTag", dataType = String.class, dataDescription = "사용자의 디바이스 식별자")
    @PreAuthorize("hasRole('USER')")
    @DeleteMapping("/api/v1/auth/unregister")
    public ApiResponse<String> unregisterUser() {
        String deviceTag = authService.unregisterUser();
        return ApiResponse.success(deviceTag);
    }

    @Operation(summary = "로그인", description = "ROLE_ANONYMOUS 이상의 권한이 필요함<br>" +
            "일반 계정일 경우 Device-Tag만 기입하면 됨")
    @PostMapping("/api/v1/auth/login")
    public ApiResponse<Object> login(
            @Valid @RequestBody AuthRequestDto requestDto
    ) {
        Object token = authService.login(requestDto);
        return ApiResponse.success(token);
    }

    @Operation(summary = "[U] 토큰 재발급", description = "ROLE_USER 이상의 권한이 필요함")
    @PreAuthorize("hasRole('USER')")
    @PostMapping("/api/v1/auth/reissue")
    public ApiResponse<TokenInfo> reissueToken(
            HttpServletRequest request
    ) {
        TokenInfo token = authService.reissueToken(request);
        return ApiResponse.success(token);
    }

    @Operation(summary = "TOTP 인증", description = "ROLE_ANONYMOUS 권한이 필요함")
    @PostMapping("/api/v1/auth/two-factor-authentication")
    public ApiResponse<TokenInfo> authenticate(
            @Valid @RequestBody TwoFactorAuthenticationRequestDto requestDto
    ) {
        TokenInfo token = authService.authenticate(requestDto);
        return ApiResponse.success(token);
    }

    @Operation(summary = "[A] TOTP 초기화", description = "ROLE_ADMIN 권한이 필요함")
    @DeleteMapping("/api/v1/auth/two-factor-authentication/{deviceTag}")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<String> resetAuthenticator(
            @PathVariable(name = "deviceTag") String deviceTag
    ) {
        String id = authService.resetAuthenticator(deviceTag);
        return ApiResponse.success(id);
    }
}
