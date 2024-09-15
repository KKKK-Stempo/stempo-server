package com.stempo.api.domain.presentation;

import com.stempo.api.domain.application.service.AuthService;
import com.stempo.api.domain.presentation.dto.request.AuthRequestDto;
import com.stempo.api.domain.presentation.dto.response.TokenInfo;
import com.stempo.api.global.common.dto.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.annotation.Secured;
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
    public ApiResponse<String> registerUser(
            @Valid @RequestBody AuthRequestDto requestDto
    ) {
        String deviceTag = authService.registerUser(requestDto);
        return ApiResponse.success(deviceTag);
    }

    @Operation(summary = "로그인", description = "ROLE_ANONYMOUS 이상의 권한이 필요함<br>" +
            "일반 계정일 경우 Device-Tag만 기입하면 됨")
    @PostMapping("/api/v1/auth/login")
    public ApiResponse<TokenInfo> login(
            @Valid @RequestBody AuthRequestDto requestDto
    ) {
        TokenInfo token = authService.login(requestDto);
        return ApiResponse.success(token);
    }

    @Operation(summary = "[U] 토큰 재발급", description = "ROLE_USER 이상의 권한이 필요함")
    @Secured({ "ROLE_USER", "ROLE_ADMIN" })
    @PostMapping("/api/v1/auth/reissue")
    public ApiResponse<TokenInfo> reissueToken(
            HttpServletRequest request
    ) {
        TokenInfo token = authService.reissueToken(request);
        return ApiResponse.success(token);
    }
}