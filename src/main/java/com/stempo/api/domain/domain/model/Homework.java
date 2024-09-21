package com.stempo.api.domain.domain.model;

import com.stempo.api.domain.presentation.dto.request.HomeworkUpdateRequestDto;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Optional;

@Getter
@Setter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Homework {

    private Long id;
    private String deviceTag;
    private String description;
    private boolean completed;

    public void update(HomeworkUpdateRequestDto requestDto) {
        Optional.ofNullable(requestDto.getDescription()).ifPresent(description -> this.description = description);
        Optional.ofNullable(requestDto.getCompleted()).ifPresent(completed -> this.completed = completed);
    }
}
