package com.stempo.api.domain.application.service;

import com.stempo.api.domain.domain.model.Article;
import com.stempo.api.domain.domain.repository.ArticleRepository;
import com.stempo.api.domain.presentation.dto.request.ArticleRequestDto;
import com.stempo.api.domain.presentation.dto.request.ArticleUpdateRequestDto;
import com.stempo.api.domain.presentation.dto.response.ArticleDetailsResponseDto;
import com.stempo.api.domain.presentation.dto.response.ArticleResponseDto;
import com.stempo.api.global.common.dto.PagedResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ArticleServiceImpl implements ArticleService {

    private final ArticleRepository repository;

    @Override
    @Transactional
    public Long registerArticle(ArticleRequestDto requestDto) {
        Article article = ArticleRequestDto.toDomain(requestDto);
        return repository.save(article).getId();
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResponseDto<ArticleResponseDto> getArticles(Pageable pageable) {
        Page<Article> articles = repository.findAll(pageable);
        return new PagedResponseDto<>(articles.map(ArticleResponseDto::toDto));
    }

    @Override
    @Transactional(readOnly = true)
    public ArticleDetailsResponseDto getArticle(Long articleId) {
        Article article = repository.findByIdOrThrow(articleId);
        return ArticleDetailsResponseDto.toDto(article);
    }

    @Override
    @Transactional
    public Long updateArticle(Long articleId, ArticleUpdateRequestDto requestDto) {
        Article article = repository.findByIdOrThrow(articleId);
        article.update(requestDto);
        return repository.save(article).getId();
    }

    @Override
    @Transactional
    public Long deleteArticle(Long articleId) {
        Article article = repository.findByIdOrThrow(articleId);
        article.delete();
        return repository.save(article).getId();
    }
}
