package com.stempo.api.domain.presentation;

import com.stempo.api.domain.application.service.ArticleService;
import com.stempo.api.domain.presentation.dto.request.ArticleRequestDto;
import com.stempo.api.domain.presentation.dto.request.ArticleUpdateRequestDto;
import com.stempo.api.domain.presentation.dto.response.ArticleDetailsResponseDto;
import com.stempo.api.domain.presentation.dto.response.ArticleResponseDto;
import com.stempo.api.global.common.dto.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Tag(name = "Article", description = "재활 관련 정보")
public class ArticleController {

    private final ArticleService articleService;

    @Operation(summary = "[A] 재활 관련 정보 등록", description = "ROLE_ADMIN 이상의 권한이 필요함")
    @Secured({ "ROLE_ADMIN" })
    @PostMapping("/api/v1/articles")
    public ApiResponse<Long> registerArticle(
            @Valid @RequestBody ArticleRequestDto requestDto
    ) {
        Long id = articleService.registerArticle(requestDto);
        return ApiResponse.success(id);
    }

    @Operation(summary = "[U] 재활 관련 정보 조회", description = "ROLE_USER 이상의 권한이 필요함")
    @Secured({ "ROLE_USER", "ROLE_ADMIN" })
    @GetMapping("/api/v1/articles")
    public ApiResponse<List<ArticleResponseDto>> getArticles() {
        List<ArticleResponseDto> articleResponseDtos = articleService.getArticles();
        return ApiResponse.success(articleResponseDtos);
    }

    @Operation(summary = "[U] 재활 관련 정보 상세 조회", description = "ROLE_USER 이상의 권한이 필요함")
    @Secured({ "ROLE_USER", "ROLE_ADMIN" })
    @GetMapping("/api/v1/articles/{articleId}")
    public ApiResponse<ArticleDetailsResponseDto> getArticle(
            @PathVariable(name = "articleId") Long articleId
    ) {
        ArticleDetailsResponseDto articleResponseDto = articleService.getArticle(articleId);
        return ApiResponse.success(articleResponseDto);
    }

    @Operation(summary = "[A] 재활 관련 정보 수정", description = "ROLE_ADMIN 이상의 권한이 필요함")
    @Secured({ "ROLE_ADMIN" })
    @PatchMapping("/api/v1/articles/{articleId}")
    public ApiResponse<Long> updateArticle(
            @PathVariable(name = "articleId") Long articleId,
            @Valid @RequestBody ArticleUpdateRequestDto requestDto
    ) {
        Long id = articleService.updateArticle(articleId, requestDto);
        return ApiResponse.success(id);
    }

    @Operation(summary = "[A] 재활 관련 정보 삭제", description = "ROLE_ADMIN 이상의 권한이 필요함")
    @Secured({ "ROLE_ADMIN" })
    @DeleteMapping("/api/v1/articles/{articleId}")
    public ApiResponse<Long> deleteArticle(
            @PathVariable(name = "articleId") Long articleId
    ) {
        Long id = articleService.deleteArticle(articleId);
        return ApiResponse.success(id);
    }
}
