package com.stempo.api.domain.application.service;

import com.stempo.api.domain.presentation.dto.request.RhythmRequestDto;

public interface RhythmService {
    String createRhythm(RhythmRequestDto requestDto);
}
