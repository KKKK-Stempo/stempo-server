package com.stempo.api.domain.presentation;

import com.stempo.api.domain.application.service.UserService;
import com.stempo.api.domain.presentation.dto.request.UserRequestDto;
import com.stempo.api.global.common.dto.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Tag(name = "User", description = "회원")
public class UserController {

    private final UserService userService;

    @Operation(summary = "회원 가입", description = "ROLE_ANONYMOUS 이상의 권한이 필요함")
    @PostMapping("/api/v1/users")
    public ApiResponse<String> registerUser(
            @Valid @RequestBody UserRequestDto requestDto
    ) {
        String deviceTag = userService.registerUser(requestDto);
        return ApiResponse.success(deviceTag);
    }
}
