package com.stempo.api.domain.presentation.dto.response;

import com.stempo.api.domain.domain.model.Article;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ArticleDetailsResponseDto {

    private Long id;
    private String title;
    private String content;
    private String thumbnailUrl;
    private String articleUrl;
    private String createdAt;

    public static ArticleDetailsResponseDto toDto(Article article) {
        return ArticleDetailsResponseDto.builder()
                .id(article.getId())
                .title(article.getTitle())
                .content(article.getContent())
                .thumbnailUrl(article.getThumbnailUrl())
                .articleUrl(article.getArticleUrl())
                .createdAt(article.getCreatedAt().toString())
                .build();
    }
}
