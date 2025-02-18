package com.stempo.dto;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class DecryptedRecord {

    private Long id;
    private String deviceTag;
    private Double accuracy;
    private Integer duration;
    private Integer steps;
    private Double leftFootAverageSpeed;
    private Double rightFootAverageSpeed;
    private Integer bit;
    private Integer bpm;
    private LocalDateTime createdAt;
}
