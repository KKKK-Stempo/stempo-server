package com.stempo.api.domain.application.service;

import com.stempo.api.domain.domain.model.Homework;
import com.stempo.api.domain.domain.repository.HomeworkRepository;
import com.stempo.api.domain.presentation.dto.request.HomeworkRequestDto;
import com.stempo.api.domain.presentation.dto.request.HomeworkUpdateRequestDto;
import com.stempo.api.domain.presentation.dto.response.HomeworkResponseDto;
import com.stempo.api.global.dto.PagedResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class HomeworkServiceImpl implements HomeworkService {

    private final UserService userService;
    private final HomeworkRepository repository;

    @Override
    @Transactional
    public Long addHomework(HomeworkRequestDto requestDto) {
        String deviceTag = userService.getCurrentDeviceTag();
        Homework homework = HomeworkRequestDto.toDomain(requestDto, deviceTag);
        return repository.save(homework).getId();
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResponseDto<HomeworkResponseDto> getHomeworks(Boolean completed, Pageable pageable) {
        Page<Homework> homeworks = repository.findByCompleted(completed, pageable);
        return new PagedResponseDto<>(homeworks.map(HomeworkResponseDto::toDto));
    }

    @Override
    @Transactional
    public Long updateHomework(Long homeworkId, HomeworkUpdateRequestDto requestDto) {
        Homework homework = repository.findByIdOrThrow(homeworkId);
        homework.update(requestDto);
        return repository.save(homework).getId();
    }

    @Override
    @Transactional
    public Long deleteHomework(Long homeworkId) {
        Homework homework = repository.findByIdOrThrow(homeworkId);
        repository.delete(homework);
        return homeworkId;
    }
}
