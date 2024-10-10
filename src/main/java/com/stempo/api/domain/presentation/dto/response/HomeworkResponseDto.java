package com.stempo.api.domain.presentation.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class HomeworkResponseDto {

    private Long id;
    private String description;
    private boolean completed;
}
