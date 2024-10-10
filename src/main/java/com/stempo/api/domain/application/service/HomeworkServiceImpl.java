package com.stempo.api.domain.application.service;

import com.stempo.api.domain.domain.model.Homework;
import com.stempo.api.domain.domain.repository.HomeworkRepository;
import com.stempo.api.domain.presentation.dto.request.HomeworkRequestDto;
import com.stempo.api.domain.presentation.dto.request.HomeworkUpdateRequestDto;
import com.stempo.api.domain.presentation.dto.response.HomeworkResponseDto;
import com.stempo.api.domain.presentation.mapper.HomeworkDtoMapper;
import com.stempo.api.global.dto.PagedResponseDto;
import com.stempo.api.global.util.EncryptionUtil;
import com.stempo.api.global.util.PaginationUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class HomeworkServiceImpl implements HomeworkService {

    private final UserService userService;
    private final HomeworkRepository repository;
    private final HomeworkDtoMapper mapper;
    private final EncryptionUtil encryptionUtil;

    @Override
    @Transactional
    public Long addHomework(HomeworkRequestDto requestDto) {
        String deviceTag = userService.getCurrentDeviceTag();
        String encryptedDescription = encryptionUtil.encrypt(requestDto.getDescription());
        Homework homework = Homework.create(deviceTag, encryptedDescription);
        return repository.save(homework).getId();
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResponseDto<HomeworkResponseDto> getHomeworks(Boolean completed, Pageable pageable) {
        Page<Homework> homeworksPage = repository.findByCompleted(completed, pageable);

        List<HomeworkResponseDto> responseDtos = homeworksPage.getContent().stream()
                .map(this::decryptAndConvertToDto)
                .toList();

        List<String> encryptedFields = List.of("description");
        if (PaginationUtils.isAnySortFieldPresent(pageable.getSort(), HomeworkResponseDto.class, encryptedFields)) {
            responseDtos = PaginationUtils.applySorting(responseDtos, pageable.getSort());
        }

        return new PagedResponseDto<>(responseDtos, pageable, responseDtos.size());
    }

    @Override
    @Transactional
    public Long updateHomework(Long homeworkId, HomeworkUpdateRequestDto requestDto) {
        Homework homework = repository.findByIdOrThrow(homeworkId);
        requestDto.setDescription(encryptionUtil.encrypt(requestDto.getDescription()));
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

    private HomeworkResponseDto decryptAndConvertToDto(Homework homework) {
        String decryptedDescription = encryptionUtil.decrypt(homework.getDescription());
        homework.setDescription(decryptedDescription);
        return mapper.toDto(homework);
    }
}
