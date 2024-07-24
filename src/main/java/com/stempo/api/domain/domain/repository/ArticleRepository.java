package com.stempo.api.domain.domain.repository;

import com.stempo.api.domain.domain.model.Article;

import java.util.List;

public interface ArticleRepository {

    Article save(Article article);

    List<Article> findAll();

    Article findByIdOrThrow(Long articleId);
}
