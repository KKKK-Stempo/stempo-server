package com.stempo.api.domain.presentation;

import com.stempo.api.domain.application.service.LoginService;
import com.stempo.api.domain.presentation.dto.response.TokenInfo;
import com.stempo.api.global.common.dto.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Tag(name = "Login", description = "로그인")
public class LoginController {

    private final LoginService loginService;

    @Operation(summary = "로그인", description = "ROLE_ANONYMOUS 이상의 권한이 필요함<br>" +
            "일반 계정일 경우 Device-Tag만 기입하면 됨")
    @PostMapping("/api/v1/login")
    public ApiResponse<TokenInfo> login(
            @RequestHeader(value = "Device-Tag", defaultValue = "490154203237518") String deviceTag,
            @RequestHeader(value = "Password", required = false) String password
    ) {
        TokenInfo token = loginService.loginOrRegister(deviceTag, password);
        return ApiResponse.success(token);
    }

    @Operation(summary = "[U] 토큰 재발급", description = "ROLE_USER 이상의 권한이 필요함")
    @Secured({ "ROLE_USER", "ROLE_ADMIN" })
    @PostMapping("/api/v1/reissue")
    public ApiResponse<TokenInfo> reissueToken(
            HttpServletRequest request
    ) {
        TokenInfo token = loginService.reissueToken(request);
        return ApiResponse.success(token);
    }
}
