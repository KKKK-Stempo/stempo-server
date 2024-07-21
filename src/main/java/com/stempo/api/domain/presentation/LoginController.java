package com.stempo.api.domain.presentation;

import com.stempo.api.domain.application.service.LoginService;
import com.stempo.api.domain.presentation.dto.response.TokenInfo;
import com.stempo.api.global.common.dto.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Tag(name = "Login", description = "로그인")
public class LoginController {

    private final LoginService loginService;

    @Operation(summary = "로그인", description = "ROLE_ANONYMOUS 이상의권한이 필요함")
    @PostMapping("/api/vi/login")
    public ApiResponse<TokenInfo> login(
            @RequestHeader("Device-Tag") String deviceTag
    ) {
        TokenInfo token = loginService.loginOrRegister(deviceTag);
        return ApiResponse.success(token);
    }

    @Operation(summary = "토큰 재발급", description = "ROLE_USER 이상의 권한이 필요함")
    @PostMapping("/api/vi/reissue")
    public ApiResponse<TokenInfo> reissueToken(
            HttpServletRequest request,
            HttpServletResponse response
    ) {
        TokenInfo token = loginService.reissueToken(request);
        response.setHeader("Authorization", "Bearer" + token.getAccessToken());
        response.setHeader("Refresh-Token", token.getRefreshToken());
        return ApiResponse.success(token);
    }
}
