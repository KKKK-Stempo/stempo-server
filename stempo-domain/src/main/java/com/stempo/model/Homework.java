package com.stempo.model;

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
    private Boolean completed;

    public static Homework create(String deviceTag, String description) {
        return Homework.builder()
                .deviceTag(deviceTag)
                .description(description)
                .completed(false)
                .build();
    }

    public void update(Homework homework) {
        Optional.ofNullable(homework.getDescription()).ifPresent(description -> this.description = description);
        Optional.of(homework.getCompleted()).ifPresent(completed -> this.completed = completed);
    }
}
