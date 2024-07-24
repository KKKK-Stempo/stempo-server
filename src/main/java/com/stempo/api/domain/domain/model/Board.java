package com.stempo.api.domain.domain.model;

import com.stempo.api.domain.presentation.dto.request.BoardUpdateRequestDto;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Getter
@Setter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Board {

    private Long id;
    private String deviceTag;
    private BoardCategory category;
    private String title;
    private String content;
    private List<String> fileUrls;
    private LocalDateTime createdAt;
    private boolean deleted;

    public void update(BoardUpdateRequestDto requestDto) {
        Optional.ofNullable(requestDto.getCategory()).ifPresent(category -> this.category = category);
        Optional.ofNullable(requestDto.getTitle()).ifPresent(title -> this.title = title);
        Optional.ofNullable(requestDto.getContent()).ifPresent(content -> this.content = content);
    }

    public void delete() {
        setDeleted(true);
    }
}
