package com.stempo.model;

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
public class Record {

    private Long id;
    private String deviceTag;
    private String accuracy;
    private String duration;
    private String steps;
    private LocalDateTime createdAt;

    public static Record create(String deviceTag, String accuracy, String duration, String steps) {
        return Record.builder()
                .deviceTag(deviceTag)
                .accuracy(accuracy)
                .duration(duration)
                .steps(steps)
                .build();
    }
}
