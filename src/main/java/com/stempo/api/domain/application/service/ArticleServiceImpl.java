package com.stempo.api.domain.application.service;

import com.stempo.api.domain.domain.model.Article;
import com.stempo.api.domain.domain.repository.ArticleRepository;
import com.stempo.api.domain.presentation.dto.request.ArticleRequestDto;
import com.stempo.api.domain.presentation.dto.request.ArticleUpdateRequestDto;
import com.stempo.api.domain.presentation.dto.response.ArticleDetailsResponseDto;
import com.stempo.api.domain.presentation.dto.response.ArticleResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ArticleServiceImpl implements ArticleService {

    private final ArticleRepository repository;

    @Override
    public Long registerArticle(ArticleRequestDto requestDto) {
        Article article = ArticleRequestDto.toDomain(requestDto);
        return repository.save(article).getId();
    }

    @Override
    public List<ArticleResponseDto> getArticles() {
        List<Article> articles = repository.findAll();
        return articles.stream()
                .map(ArticleResponseDto::toDto)
                .toList();
    }

    @Override
    public ArticleDetailsResponseDto getArticle(Long articleId) {
        Article article = repository.findByIdOrThrow(articleId);
        return ArticleDetailsResponseDto.toDto(article);
    }

    @Override
    public Long updateArticle(Long articleId, ArticleUpdateRequestDto requestDto) {
        Article article = repository.findByIdOrThrow(articleId);
        article.update(requestDto);
        return repository.save(article).getId();
    }

    @Override
    public Long deleteArticle(Long articleId) {
        Article article = repository.findByIdOrThrow(articleId);
        article.delete();
        return repository.save(article).getId();
    }
}
