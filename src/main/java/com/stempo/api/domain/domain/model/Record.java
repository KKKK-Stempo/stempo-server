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
public class Record {

    private Long id;
    private String deviceTag;
    private Double accuracy;
    private Integer duration;
    private Integer steps;
    private LocalDateTime createdAt;
}
