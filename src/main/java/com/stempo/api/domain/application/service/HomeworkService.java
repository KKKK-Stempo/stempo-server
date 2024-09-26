package com.stempo.api.domain.application.service;

import com.stempo.api.domain.presentation.dto.request.HomeworkRequestDto;
import com.stempo.api.domain.presentation.dto.request.HomeworkUpdateRequestDto;
import com.stempo.api.domain.presentation.dto.response.HomeworkResponseDto;
import com.stempo.api.global.dto.PagedResponseDto;
import org.springframework.data.domain.Pageable;

public interface HomeworkService {

    Long addHomework(HomeworkRequestDto requestDto);

    PagedResponseDto<HomeworkResponseDto> getHomeworks(Boolean completed, Pageable pageable);

    Long updateHomework(Long homeworkId, HomeworkUpdateRequestDto requestDto);

    Long deleteHomework(Long homeworkId);
}
