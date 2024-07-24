package com.stempo.api.domain.presentation.dto.response;

import com.stempo.api.domain.domain.model.Article;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ArticleResponseDto {

    private Long id;
    private String title;
    private String content;
    private String thumbnailUrl;
    private String articleUrl;
    private String createdAt;

    public static ArticleResponseDto toDto(Article article) {
        return ArticleResponseDto.builder()
                .id(article.getId())
                .title(article.getTitle())
                .content(article.getContent())
                .thumbnailUrl(article.getThumbnailUrl())
                .articleUrl(article.getArticleUrl())
                .createdAt(article.getCreatedAt().toString())
                .build();
    }
}
