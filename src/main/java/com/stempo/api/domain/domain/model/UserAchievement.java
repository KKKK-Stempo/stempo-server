package com.stempo.api.domain.domain.model;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class UserAchievement {

    private Long id;
    private String deviceTag;
    private Long achievementId;
    private LocalDateTime createdAt;

    public static UserAchievement create(Long achievementId, String deviceTag) {
        return UserAchievement.builder()
                .achievementId(achievementId)
                .deviceTag(deviceTag)
                .build();
    }
}
