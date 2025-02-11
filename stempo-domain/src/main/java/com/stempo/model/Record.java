package com.stempo.model;

import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
    private String leftFootAverageSpeed;
    private String rightFootAverageSpeed;
    private String bit;
    private String bpm;
    private LocalDateTime createdAt;

    public static Record create(
        String deviceTag,
        String accuracy,
        String duration,
        String steps,
        String leftFootAverageSpeed,
        String rightFootAverageSpeed,
        String bit,
        String bpm
    ) {
        return Record.builder()
            .deviceTag(deviceTag)
            .accuracy(accuracy)
            .duration(duration)
            .steps(steps)
            .leftFootAverageSpeed(leftFootAverageSpeed)
            .rightFootAverageSpeed(rightFootAverageSpeed)
            .bit(bit)
            .bpm(bpm)
            .build();
    }
}
