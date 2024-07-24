package com.stempo.api.domain.application.service;

import com.stempo.api.domain.presentation.dto.request.ArticleRequestDto;
import com.stempo.api.domain.presentation.dto.request.ArticleUpdateRequestDto;
import com.stempo.api.domain.presentation.dto.response.ArticleDetailsResponseDto;
import com.stempo.api.domain.presentation.dto.response.ArticleResponseDto;

import java.util.List;

public interface ArticleService {

    Long registerArticle(ArticleRequestDto requestDto);

    List<ArticleResponseDto> getArticles();

    ArticleDetailsResponseDto getArticle(Long articleId);

    Long updateArticle(Long articleId, ArticleUpdateRequestDto requestDto);

    Long deleteArticle(Long articleId);
}
