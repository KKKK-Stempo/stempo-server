package com.stempo.api.domain.application.service;

import com.stempo.api.domain.presentation.dto.request.ArticleRequestDto;
import com.stempo.api.domain.presentation.dto.request.ArticleUpdateRequestDto;
import com.stempo.api.domain.presentation.dto.response.ArticleDetailsResponseDto;
import com.stempo.api.domain.presentation.dto.response.ArticleResponseDto;
import com.stempo.api.global.common.dto.PagedResponseDto;
import org.springframework.data.domain.Pageable;

public interface ArticleService {

    Long registerArticle(ArticleRequestDto requestDto);

    PagedResponseDto<ArticleResponseDto> getArticles(Pageable pageable);

    ArticleDetailsResponseDto getArticle(Long articleId);

    Long updateArticle(Long articleId, ArticleUpdateRequestDto requestDto);

    Long deleteArticle(Long articleId);
}
