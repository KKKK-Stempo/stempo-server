package com.stempo.api.domain.domain.repository;

import com.stempo.api.domain.domain.model.Article;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ArticleRepository {

    Article save(Article article);

    Page<Article> findAll(Pageable pageable);

    Article findByIdOrThrow(Long articleId);
}
