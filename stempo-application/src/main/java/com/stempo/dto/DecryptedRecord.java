package com.stempo.dto;

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
}
