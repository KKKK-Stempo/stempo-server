package com.stempo.api.domain.presentation;

import com.stempo.api.domain.application.service.UserAchievementService;
import com.stempo.api.domain.presentation.dto.response.UserAchievementResponseDto;
import com.stempo.api.global.common.dto.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Tag(name = "Achievement - User", description = "유저 업적")
public class UserAchievementController {

    private final UserAchievementService userAchievementService;

    @Operation(summary = "[U] 유저 업적 등록", description = "ROLE_USER 이상의 권한이 필요함")
    @Secured({ "ROLE_USER", "ROLE_ADMIN" })
    @PostMapping("/api/v1/achievements/user/{achievementId}")
    public ApiResponse<Long> registerUserAchievement(
            @PathVariable(name = "achievementId") Long achievementId
    ) {
        Long id = userAchievementService.registerUserAchievement(achievementId);
        return ApiResponse.success(id);
    }

    @Operation(summary = "[U] 내 업적 조회", description = "ROLE_USER 이상의 권한이 필요함")
    @Secured({ "ROLE_USER", "ROLE_ADMIN" })
    @GetMapping("/api/v1/achievements/user")
    public ApiResponse<List<UserAchievementResponseDto>> getUserAchievements() {
        List<UserAchievementResponseDto> myAchievements = userAchievementService.getUserAchievements();
        return ApiResponse.success(myAchievements);
    }
}
