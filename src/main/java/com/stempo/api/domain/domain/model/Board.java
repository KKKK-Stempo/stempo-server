package com.stempo.api.domain.domain.model;

import com.stempo.api.domain.presentation.dto.request.BoardUpdateRequestDto;
import com.stempo.api.global.exception.PermissionDeniedException;
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

    public void update(BoardUpdateRequestDto requestDto) {
        Optional.ofNullable(requestDto.getCategory()).ifPresent(category -> this.category = category);
        Optional.ofNullable(requestDto.getTitle()).ifPresent(title -> this.title = title);
        Optional.ofNullable(requestDto.getContent()).ifPresent(content -> this.content = content);
        Optional.ofNullable(requestDto.getFileUrls()).ifPresent(fileUrls -> this.fileUrls = fileUrls);
    }

    public boolean isOwner(User user) {
        return this.deviceTag.equals(user.getDeviceTag());
    }

    public void validateAccessPermission(User user) {
        if (!(isOwner(user) || user.isAdmin())) {
            throw new PermissionDeniedException("게시글을 수정/삭제할 권한이 없습니다.");
        }
    }

    public void validateAccessPermissionForNotice(User user) {
        if (isNotice() && !user.isAdmin()) {
            throw new PermissionDeniedException("공지사항 관리 권한이 없습니다.");
        }
    }

    public boolean isNotice() {
        return category.equals(BoardCategory.NOTICE);
    }
}
