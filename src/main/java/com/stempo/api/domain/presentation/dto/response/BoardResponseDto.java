package com.stempo.api.domain.presentation.dto.response;

import com.stempo.api.domain.domain.model.BoardCategory;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class BoardResponseDto {

    private Long id;
    private String deviceTag;
    private BoardCategory category;
    private String title;
    private String content;
    private List<String> fileUrls;
    private String createdAt;
}
