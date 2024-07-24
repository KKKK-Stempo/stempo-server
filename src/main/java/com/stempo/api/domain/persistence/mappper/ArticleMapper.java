package com.stempo.api.domain.persistence.mappper;

import com.stempo.api.domain.domain.model.Article;
import com.stempo.api.domain.persistence.entity.ArticleEntity;
import org.springframework.stereotype.Component;

@Component
public class ArticleMapper {

    public ArticleEntity toEntity(Article article) {
        return ArticleEntity.builder()
                .id(article.getId())
                .title(article.getTitle())
                .content(article.getContent())
                .thumbnailUrl(article.getThumbnailUrl())
                .articleUrl(article.getArticleUrl())
                .deleted(article.isDeleted())
                .build();
    }

    public Article toDomain(ArticleEntity entity) {
        return Article.builder()
                .id(entity.getId())
                .title(entity.getTitle())
                .content(entity.getContent())
                .thumbnailUrl(entity.getThumbnailUrl())
                .articleUrl(entity.getArticleUrl())
                .createdAt(entity.getCreatedAt())
                .deleted(entity.isDeleted())
                .build();
    }
}
