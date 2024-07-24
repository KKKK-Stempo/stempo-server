package com.stempo.api.domain.persistence.repository;

import com.stempo.api.domain.domain.model.Article;
import com.stempo.api.domain.domain.repository.ArticleRepository;
import com.stempo.api.domain.persistence.entity.ArticleEntity;
import com.stempo.api.domain.persistence.mappper.ArticleMapper;
import com.stempo.api.global.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

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
    public List<Article> findAll() {
        List<ArticleEntity> articles = repository.findAllArticles();
        return articles.stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public Article findByIdOrThrow(Long articleId) {
        return repository.findByIdAndNotDeleted(articleId)
                .map(mapper::toDomain)
                .orElseThrow(() -> new NotFoundException("[Article] id: " + articleId + " not found"));
    }
}
