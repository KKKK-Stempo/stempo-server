package com.stempo.api.domain.domain.model;

import com.stempo.api.domain.presentation.dto.request.ArticleUpdateRequestDto;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Optional;

@Getter
@Setter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Article {

    private Long id;
    private String title;
    private String content;
    private String thumbnailUrl;
    private String articleUrl;
    private LocalDateTime createdAt;
    private boolean deleted;

    public void update(ArticleUpdateRequestDto requestDto) {
        Optional.ofNullable(requestDto.getTitle()).ifPresent(this::setTitle);
        Optional.ofNullable(requestDto.getContent()).ifPresent(this::setContent);
        Optional.ofNullable(requestDto.getThumbnailUrl()).ifPresent(this::setThumbnailUrl);
        Optional.ofNullable(requestDto.getArticleUrl()).ifPresent(this::setArticleUrl);
    }

    public void delete() {
        setDeleted(true);
    }
}
