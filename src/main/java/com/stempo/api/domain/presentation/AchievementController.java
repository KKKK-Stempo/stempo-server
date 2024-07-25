package com.stempo.api.domain.presentation;

import com.stempo.api.domain.application.service.AchievementService;
import com.stempo.api.domain.presentation.dto.request.AchievementRequestDto;
import com.stempo.api.domain.presentation.dto.request.AchievementUpdateRequestDto;
import com.stempo.api.domain.presentation.dto.response.AchievementResponseDto;
import com.stempo.api.global.common.dto.ApiResponse;
import com.stempo.api.global.common.dto.PagedResponseDto;
import com.stempo.api.global.exception.InvalidColumnException;
import com.stempo.api.global.exception.SortingArgumentException;
import com.stempo.api.global.util.PageableUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Tag(name = "Achievement", description = "업적")
public class AchievementController {

    private final AchievementService achievementService;
    private final PageableUtil pageableUtil;

    @Operation(summary = "[A] 업적 추가", description = "ROLE_ADMIN 이상의 권한이 필요함")
    @Secured({ "ROLE_ADMIN" })
    @PostMapping("/api/v1/achievements")
    public ApiResponse<Long> registerAchievement(
            @Valid @RequestBody AchievementRequestDto requestDto
    ) {
        Long id = achievementService.registerAchievement(requestDto);
        return ApiResponse.success(id);
    }

    @Operation(summary = "[U] 업적 목록 조회", description = "ROLE_USER 이상의 권한이 필요함<br>" +
            "DTO의 필드명을 기준으로 정렬 가능하며, 정렬 방향은 오름차순(asc)과 내림차순(desc)이 가능함")
    @Secured({ "ROLE_USER", "ROLE_ADMIN" })
    @GetMapping("/api/v1/achievements")
    public ApiResponse<PagedResponseDto<AchievementResponseDto>> getAchievements(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "20") int size,
            @RequestParam(name = "sortBy", defaultValue = "id") List<String> sortBy,
            @RequestParam(name = "sortDirection", defaultValue = "desc") List<String> sortDirection
    ) throws InvalidColumnException, SortingArgumentException {
        Pageable pageable = pageableUtil.createPageable(page, size, sortBy, sortDirection, AchievementResponseDto.class);
        PagedResponseDto<AchievementResponseDto> achievements = achievementService.getAchievements(pageable);
        return ApiResponse.success(achievements);
    }

    @Operation(summary = "[A] 업적 수정", description = "ROLE_ADMIN 이상의 권한이 필요함")
    @Secured({ "ROLE_ADMIN" })
    @PatchMapping("/api/v1/achievements/{achievementId}")
    public ApiResponse<Long> updateAchievement(
            @PathVariable(name = "achievementId") Long achievementId,
            @Valid @RequestBody AchievementUpdateRequestDto requestDto
    ) {
        Long id = achievementService.updateAchievement(achievementId, requestDto);
        return ApiResponse.success(id);
    }

    @Operation(summary = "[A] 업적 삭제", description = "ROLE_ADMIN 이상의 권한이 필요함")
    @Secured({ "ROLE_ADMIN" })
    @DeleteMapping("/api/v1/achievements/{achievementId}")
    public ApiResponse<Long> deleteAchievement(
            @PathVariable(name = "achievementId") Long achievementId
    ) {
        Long id = achievementService.deleteAchievement(achievementId);
        return ApiResponse.success(id);
    }

}
