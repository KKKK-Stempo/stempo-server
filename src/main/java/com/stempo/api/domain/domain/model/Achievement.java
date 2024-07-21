package com.stempo.api.domain.domain.model;

import com.stempo.api.domain.presentation.dto.request.AchievementUpdateRequestDto;
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
public class Achievement {

    private Long id;
    private String name;
    private String description;
    private String imageUrl;
    private boolean deleted;

    public void update(AchievementUpdateRequestDto requestDto) {
        Optional.ofNullable(requestDto.getName()).ifPresent(this::setName);
        Optional.ofNullable(requestDto.getDescription()).ifPresent(this::setDescription);
        Optional.ofNullable(requestDto.getImageUrl()).ifPresent(this::setImageUrl);
    }

    public void delete() {
        setDeleted(true);
    }
}
