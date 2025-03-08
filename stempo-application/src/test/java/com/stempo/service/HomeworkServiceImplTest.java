package com.stempo.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyList;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.stempo.dto.DecryptedHomework;
import com.stempo.dto.PagedResponseDto;
import com.stempo.dto.request.HomeworkRequestDto;
import com.stempo.dto.request.HomeworkUpdateRequestDto;
import com.stempo.dto.response.HomeworkResponseDto;
import com.stempo.mapper.HomeworkDtoMapper;
import com.stempo.model.Homework;
import com.stempo.repository.HomeworkRepository;
import com.stempo.util.EncryptionUtils;
import com.stempo.util.PaginationUtils;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

@ExtendWith(MockitoExtension.class)
class HomeworkServiceImplTest {

    @Mock
    private UserService userService;

    @Mock
    private HomeworkDecryptionService homeworkDecryptionService;

    @Mock
    private HomeworkRepository repository;

    @Mock
    private HomeworkDtoMapper mapper;

    @Mock
    private EncryptionUtils encryptionUtils;

    @InjectMocks
    private HomeworkServiceImpl homeworkService;

    private HomeworkRequestDto homeworkRequestDto;

    private HomeworkUpdateRequestDto homeworkUpdateRequestDto;

    private Homework homework;

    @BeforeEach
    void setUp() {
        homeworkRequestDto = new HomeworkRequestDto();
        homeworkRequestDto.setDescription("Test Description");

        homeworkUpdateRequestDto = new HomeworkUpdateRequestDto();
        homeworkUpdateRequestDto.setDescription("Updated Description");
        homeworkUpdateRequestDto.setCompleted(true);

        homework = Homework.builder()
            .id(1L)
            .deviceTag("encrypted-device-tag")
            .description("encrypted-description")
            .completed(false)
            .build();
    }

    @Test
    void 과제를_정상적으로_추가한다() {
        // given
        String deviceTag = "encrypted-device-tag";
        String encryptedDescription = "encrypted-description";

        when(encryptionUtils.encrypt(anyString())).thenReturn(encryptedDescription);
        when(repository.save(any(Homework.class))).thenReturn(homework);

        // when
        Long resultId = homeworkService.addHomework(deviceTag, homeworkRequestDto);

        // then
        assertThat(resultId).isEqualTo(homework.getId());
        verify(repository).save(any(Homework.class));
    }

    @Test
    void 완료여부에_따라_과제목록을_반환한다() {
        // given
        Boolean completed = false;
        Sort sort = Sort.by("description").ascending();
        Pageable pageable = PageRequest.of(0, 10, sort);
        String deviceTag = "someDeviceTag"; // 추가

        // Homework 객체 생성
        Homework homework = Homework.builder()
            .id(1L)
            .deviceTag(deviceTag) // 추가
            .description("Encrypted Description")
            .completed(false)
            .build();
        List<Homework> homeworkList = List.of(homework);

        HomeworkResponseDto responseDto = HomeworkResponseDto.builder()
            .id(homework.getId())
            .description("Decrypted Description")
            .completed(homework.getCompleted())
            .build();

        DecryptedHomework decryptedHomework = DecryptedHomework.builder()
            .id(homework.getId())
            .deviceTag(deviceTag)
            .description("Decrypted Description")
            .completed(homework.getCompleted())
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .build();

        when(repository.findByDeviceTagAndCompleted(deviceTag, completed, pageable))
            .thenReturn(new PageImpl<>(homeworkList, pageable, homeworkList.size()));
        when(homeworkDecryptionService.decryptHomework(any(Homework.class))).thenReturn(decryptedHomework);
        doReturn(responseDto).when(mapper).toDto(decryptedHomework);

        try (MockedStatic<PaginationUtils> mockedStatic = mockStatic(PaginationUtils.class)) {
            mockedStatic.when(
                    () -> PaginationUtils.isAnySortFieldPresent(any(Sort.class), eq(HomeworkResponseDto.class),
                        anyList()))
                .thenReturn(true);
            mockedStatic.when(() -> PaginationUtils.applySorting(anyList(), any(Sort.class)))
                .thenReturn(List.of(responseDto));

            // when
            PagedResponseDto<HomeworkResponseDto> result = homeworkService.getHomeworks(deviceTag, completed, pageable);

            // then
            assertThat(result.getItems()).hasSize(1);
            assertThat(result.getItems().getFirst().getDescription()).isEqualTo("Decrypted Description");
            verify(repository).findByDeviceTagAndCompleted(deviceTag, completed, pageable);
            verify(homeworkDecryptionService).decryptHomework(any(Homework.class));
            verify(mapper).toDto(decryptedHomework);
        }
    }

    @Test
    void 과제를_수정한다() {
        // given
        Long homeworkId = homework.getId();
        Homework existingHomework = homework;

        when(repository.findByIdOrThrow(homeworkId)).thenReturn(existingHomework);
        when(encryptionUtils.encrypt(anyString())).thenReturn("encrypted-updated-description");
        when(mapper.toDomain(any(HomeworkUpdateRequestDto.class))).thenReturn(
            Homework.builder()
                .description("encrypted-updated-description")
                .completed(true)
                .build()
        );
        when(repository.save(any(Homework.class))).thenReturn(existingHomework);

        // when
        Long resultId = homeworkService.updateHomework(homeworkId, homeworkUpdateRequestDto);

        // then
        assertThat(resultId).isEqualTo(homeworkId);
        verify(repository).findByIdOrThrow(homeworkId);
        verify(encryptionUtils).encrypt(anyString());
        verify(mapper).toDomain(any(HomeworkUpdateRequestDto.class));
        verify(repository).save(any(Homework.class));
    }

    @Test
    void 과제를_삭제한다() {
        // given
        Long homeworkId = homework.getId();
        when(repository.findByIdOrThrow(homeworkId)).thenReturn(homework);

        // when
        Long resultId = homeworkService.deleteHomework(homeworkId);

        // then
        assertThat(resultId).isEqualTo(homeworkId);
        verify(repository).findByIdOrThrow(homeworkId);
        verify(repository).delete(homework);
    }

    @Test
    void 디바이스_태그로_과제_목록을_복호화하여_조회한다() {
        // given
        List<String> deviceTags = List.of("device1", "device2");
        List<String> encryptedDeviceTags = List.of("encrypted1", "encrypted2");

        Homework homework1 = Homework.builder()
            .id(1L)
            .deviceTag("encrypted1")
            .description("encryptedDesc1")
            .completed(false)
            .build();
        Homework homework2 = Homework.builder()
            .id(2L)
            .deviceTag("encrypted2")
            .description("encryptedDesc2")
            .completed(true)
            .build();
        List<Homework> homeworkList = List.of(homework1, homework2);

        DecryptedHomework decryptedHomework1 = DecryptedHomework.builder()
            .id(1L)
            .deviceTag("decrypted1")
            .description("desc1")
            .completed(false)
            .build();
        DecryptedHomework decryptedHomework2 = DecryptedHomework.builder()
            .id(2L)
            .deviceTag("decrypted2")
            .description("desc2")
            .completed(true)
            .build();

        when(userService.encryptDeviceTag("device1")).thenReturn("encrypted1");
        when(userService.encryptDeviceTag("device2")).thenReturn("encrypted2");
        when(repository.findHomeworkByDeviceTags(encryptedDeviceTags)).thenReturn(homeworkList);
        when(homeworkDecryptionService.decryptHomework(homework1)).thenReturn(decryptedHomework1);
        when(homeworkDecryptionService.decryptHomework(homework2)).thenReturn(decryptedHomework2);

        // when
        List<DecryptedHomework> result = homeworkService.getByDeviceTags(deviceTags);

        // then
        assertThat(result).containsExactly(decryptedHomework1, decryptedHomework2);
        verify(userService).encryptDeviceTag("device1");
        verify(userService).encryptDeviceTag("device2");
        verify(repository).findHomeworkByDeviceTags(encryptedDeviceTags);
        verify(homeworkDecryptionService).decryptHomework(homework1);
        verify(homeworkDecryptionService).decryptHomework(homework2);
    }
}
