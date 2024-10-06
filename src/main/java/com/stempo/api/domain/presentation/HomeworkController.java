package com.stempo.api.domain.presentation;

import com.stempo.api.domain.application.service.HomeworkService;
import com.stempo.api.domain.presentation.dto.request.HomeworkRequestDto;
import com.stempo.api.domain.presentation.dto.request.HomeworkUpdateRequestDto;
import com.stempo.api.domain.presentation.dto.response.HomeworkResponseDto;
import com.stempo.api.global.dto.ApiResponse;
import com.stempo.api.global.dto.PagedResponseDto;
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
@Tag(name = "Homework", description = "과제")
public class HomeworkController {

    private final HomeworkService homeworkService;
    private final PageableUtil pageableUtil;

    @Operation(summary = "[U] 과제 추가", description = "ROLE_USER 이상의 권한이 필요함")
    @Secured({ "ROLE_USER", "ROLE_ADMIN" })
    @PostMapping(value = "/api/v1/homeworks")
    public ApiResponse<Long> addHomework(
            @Valid @RequestBody HomeworkRequestDto requestDto
    ) {
        Long id = homeworkService.addHomework(requestDto);
        return ApiResponse.success(id);
    }

    @Operation(summary = "[U] 과제 조회", description = "ROLE_USER 이상의 권한이 필요함<br>" +
            "completed(Optional)가 true이면 완료된 과제, false이면 미완료된 과제를 조회함<br>" +
            "completed가 없으면 모든 과제를 조회함<br>" +
            "DTO의 필드명을 기준으로 정렬 가능하며, 정렬 방향은 오름차순(asc)과 내림차순(desc)이 가능함")
    @Secured({ "ROLE_USER", "ROLE_ADMIN" })
    @GetMapping(value = "/api/v1/homeworks")
    public ApiResponse<PagedResponseDto<HomeworkResponseDto>> getHomeworks(
            @RequestParam(name = "completed", required = false) Boolean completed,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "20") int size,
            @RequestParam(name = "sortBy", defaultValue = "completed, id") List<String> sortBy,
            @RequestParam(name = "sortDirection", defaultValue = "asc, asc") List<String> sortDirection
    ) throws InvalidColumnException, SortingArgumentException {
        Pageable pageable = pageableUtil.createPageable(page, size, sortBy, sortDirection, HomeworkResponseDto.class);
        PagedResponseDto<HomeworkResponseDto> homeworks = homeworkService.getHomeworks(completed, pageable);
        return ApiResponse.success(homeworks);
    }

    @Operation(summary = "[U] 과제 수정", description = "ROLE_USER 이상의 권한이 필요함")
    @Secured({ "ROLE_USER", "ROLE_ADMIN" })
    @PatchMapping(value = "/api/v1/homeworks/{homeworkId}")
    public ApiResponse<Long> updateHomework(
            @PathVariable(name = "homeworkId") Long homeworkId,
            @Valid @RequestBody HomeworkUpdateRequestDto requestDto
    ) {
        Long id = homeworkService.updateHomework(homeworkId, requestDto);
        return ApiResponse.success(id);
    }

    @Operation(summary = "[U] 과제 삭제", description = "ROLE_USER 이상의 권한이 필요함")
    @Secured({ "ROLE_USER", "ROLE_ADMIN" })
    @DeleteMapping(value = "/api/v1/homeworks/{homeworkId}")
    public ApiResponse<Long> deleteHomework(
            @PathVariable(name = "homeworkId") Long homeworkId
    ) {
        Long id = homeworkService.deleteHomework(homeworkId);
        return ApiResponse.success(id);
    }
}
