package com.stempo.api.domain.presentation;

import com.stempo.api.domain.application.service.UserAchievementService;
import com.stempo.api.domain.presentation.dto.response.UserAchievementResponseDto;
import com.stempo.api.global.common.dto.ApiResponse;
import com.stempo.api.global.common.dto.PagedResponseDto;
import com.stempo.api.global.exception.InvalidColumnException;
import com.stempo.api.global.exception.SortingArgumentException;
import com.stempo.api.global.util.PageableUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Tag(name = "Achievement - User", description = "유저 업적")
public class UserAchievementController {

    private final UserAchievementService userAchievementService;
    private final PageableUtil pageableUtil;

    @Operation(summary = "[U] 유저 업적 등록", description = "ROLE_USER 이상의 권한이 필요함")
    @Secured({ "ROLE_USER", "ROLE_ADMIN" })
    @PostMapping("/api/v1/achievements/user/{achievementId}")
    public ApiResponse<Long> registerUserAchievement(
            @PathVariable(name = "achievementId") Long achievementId
    ) {
        Long id = userAchievementService.registerUserAchievement(achievementId);
        return ApiResponse.success(id);
    }

    @Operation(summary = "[U] 내 업적 조회", description = "ROLE_USER 이상의 권한이 필요함<br>" +
            "DTO의 필드명을 기준으로 정렬 가능하며, 정렬 방향은 오름차순(asc)과 내림차순(desc)이 가능함")
    @Secured({ "ROLE_USER", "ROLE_ADMIN" })
    @GetMapping("/api/v1/achievements/user")
    public ApiResponse<PagedResponseDto<UserAchievementResponseDto>> getUserAchievements(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "20") int size,
            @RequestParam(name = "sortBy", defaultValue = "createdAt") List<String> sortBy,
            @RequestParam(name = "sortDirection", defaultValue = "desc") List<String> sortDirection
    ) throws InvalidColumnException, SortingArgumentException {
        Pageable pageable = pageableUtil.createPageable(page, size, sortBy, sortDirection, UserAchievementResponseDto.class);
        PagedResponseDto<UserAchievementResponseDto> myAchievements = userAchievementService.getUserAchievements(pageable);
        return ApiResponse.success(myAchievements);
    }
}
