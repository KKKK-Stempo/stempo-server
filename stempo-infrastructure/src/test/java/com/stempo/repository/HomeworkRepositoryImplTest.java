package com.stempo.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.anyBoolean;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.stempo.entity.HomeworkEntity;
import com.stempo.exception.NotFoundException;
import com.stempo.mapper.HomeworkMapper;
import com.stempo.model.Homework;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

class HomeworkRepositoryImplTest {

    @Mock
    private HomeworkJpaRepository homeworkJpaRepository;

    @Mock
    private HomeworkMapper homeworkMapper;

    @InjectMocks
    private HomeworkRepositoryImpl homeworkRepository;

    private Homework homework;
    private HomeworkEntity homeworkEntity;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        homework = Homework.builder()
                .id(1L)
                .deviceTag("device123")
                .description("Test description")
                .completed(false)
                .build();

        homeworkEntity = HomeworkEntity.builder()
                .id(1L)
                .deviceTag("device123")
                .description("Test description")
                .completed(false)
                .build();
    }

    @Test
    void 과제를_저장한다() {
        // given
        when(homeworkMapper.toEntity(any(Homework.class))).thenReturn(homeworkEntity);
        when(homeworkJpaRepository.save(any(HomeworkEntity.class))).thenReturn(homeworkEntity);
        when(homeworkMapper.toDomain(any(HomeworkEntity.class))).thenReturn(homework);

        // when
        Homework savedHomework = homeworkRepository.save(homework);

        // then
        assertThat(savedHomework).isEqualTo(homework);
        verify(homeworkJpaRepository, times(1)).save(homeworkEntity);
    }

    @Test
    void 과제를_삭제한다() {
        // given
        when(homeworkMapper.toEntity(any(Homework.class))).thenReturn(homeworkEntity);

        // when
        homeworkRepository.delete(homework);

        // then
        verify(homeworkJpaRepository, times(1)).delete(homeworkEntity);
    }

    @Test
    void 과제를_모두_삭제한다() {
        // given
        when(homeworkMapper.toEntity(any(Homework.class))).thenReturn(homeworkEntity);

        // when
        homeworkRepository.deleteAll(List.of(homework));

        // then
        verify(homeworkJpaRepository, times(1)).deleteAll(List.of(homeworkEntity));
    }

    @Test
    void 완료된_과제를_페이지_단위로_조회한다() {
        // given
        Pageable pageable = mock(Pageable.class);
        Page<HomeworkEntity> homeworkEntityPage = new PageImpl<>(List.of(homeworkEntity));
        when(homeworkJpaRepository.findByCompleted(anyBoolean(), any(Pageable.class))).thenReturn(homeworkEntityPage);
        when(homeworkMapper.toDomain(any(HomeworkEntity.class))).thenReturn(homework);

        // when
        Page<Homework> homeworkPage = homeworkRepository.findByCompleted(true, pageable);

        // then
        assertThat(homeworkPage.getContent()).hasSize(1);
        assertThat(homeworkPage.getContent().getFirst()).isEqualTo(homework);
        verify(homeworkJpaRepository, times(1)).findByCompleted(true, pageable);
    }

    @Test
    void ID로_과제를_조회한다_존재하지_않을_때_예외를_발생시킨다() {
        // given
        when(homeworkJpaRepository.findById(anyLong())).thenReturn(Optional.empty());

        // when, then
        assertThrows(NotFoundException.class, () -> homeworkRepository.findByIdOrThrow(1L));
    }

    @Test
    void ID로_과제를_조회한다() {
        // given
        when(homeworkJpaRepository.findById(anyLong())).thenReturn(Optional.of(homeworkEntity));
        when(homeworkMapper.toDomain(any(HomeworkEntity.class))).thenReturn(homework);

        // when
        Homework foundHomework = homeworkRepository.findByIdOrThrow(1L);

        // then
        assertThat(foundHomework).isEqualTo(homework);
        verify(homeworkJpaRepository, times(1)).findById(1L);
    }

    @Test
    void 디바이스_태그로_과제를_조회한다() {
        // given
        List<HomeworkEntity> homeworkEntities = List.of(homeworkEntity);
        when(homeworkJpaRepository.findByDeviceTag(anyString())).thenReturn(homeworkEntities);
        when(homeworkMapper.toDomain(any(HomeworkEntity.class))).thenReturn(homework);

        // when
        List<Homework> homeworkList = homeworkRepository.findByDeviceTag("device123");

        // then
        assertThat(homeworkList).hasSize(1);
        assertThat(homeworkList.getFirst()).isEqualTo(homework);
        verify(homeworkJpaRepository, times(1)).findByDeviceTag("device123");
    }
}
