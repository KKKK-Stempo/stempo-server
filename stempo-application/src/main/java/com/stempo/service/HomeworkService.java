package com.stempo.service;

import com.stempo.dto.PagedResponseDto;
import com.stempo.dto.request.HomeworkRequestDto;
import com.stempo.dto.request.HomeworkUpdateRequestDto;
import com.stempo.dto.response.HomeworkResponseDto;
import org.springframework.data.domain.Pageable;

public interface HomeworkService {

    Long addHomework(HomeworkRequestDto requestDto);

    PagedResponseDto<HomeworkResponseDto> getHomeworks(Boolean completed, Pageable pageable);

    Long updateHomework(Long homeworkId, HomeworkUpdateRequestDto requestDto);

    Long deleteHomework(Long homeworkId);
}
