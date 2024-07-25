package com.stempo.api.domain.persistence.repository;

import com.stempo.api.domain.domain.model.Article;
import com.stempo.api.domain.domain.repository.ArticleRepository;
import com.stempo.api.domain.persistence.entity.ArticleEntity;
import com.stempo.api.domain.persistence.mappper.ArticleMapper;
import com.stempo.api.global.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ArticleRepositoryImpl implements ArticleRepository {

    private final ArticleJpaRepository repository;
    private final ArticleMapper mapper;


    @Override
    public Article save(Article article) {
        ArticleEntity jpaEntity = mapper.toEntity(article);
        ArticleEntity savedEntity = repository.save(jpaEntity);
        return mapper.toDomain(savedEntity);
    }

    @Override
    public Page<Article> findAll(Pageable pageable) {
        Page<ArticleEntity> articles = repository.findAllArticles(pageable);
        return articles.map(mapper::toDomain);
    }

    @Override
    public Article findByIdOrThrow(Long articleId) {
        return repository.findByIdAndNotDeleted(articleId)
                .map(mapper::toDomain)
                .orElseThrow(() -> new NotFoundException("[Article] id: " + articleId + " not found"));
    }
}
