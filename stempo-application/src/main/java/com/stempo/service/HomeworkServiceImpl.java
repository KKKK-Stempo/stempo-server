package com.stempo.service;

import com.stempo.dto.PagedResponseDto;
import com.stempo.dto.request.HomeworkRequestDto;
import com.stempo.dto.request.HomeworkUpdateRequestDto;
import com.stempo.dto.response.HomeworkResponseDto;
import com.stempo.mapper.HomeworkDtoMapper;
import com.stempo.model.Homework;
import com.stempo.repository.HomeworkRepository;
import com.stempo.util.EncryptionUtils;
import com.stempo.util.PaginationUtils;
import java.util.List;
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
    private final HomeworkDtoMapper mapper;
    private final EncryptionUtils encryptionUtils;

    @Override
    @Transactional
    public Long addHomework(HomeworkRequestDto requestDto) {
        String deviceTag = userService.getCurrentDeviceTag();
        String encryptedDescription = encryptionUtils.encrypt(requestDto.getDescription());
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
        requestDto.setDescription(encryptionUtils.encrypt(requestDto.getDescription()));
        Homework updatedHomework = mapper.toDomain(requestDto);
        homework.update(updatedHomework);
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
        String decryptedDescription = encryptionUtils.decrypt(homework.getDescription());
        homework.setDescription(decryptedDescription);
        return mapper.toDto(homework);
    }
}
