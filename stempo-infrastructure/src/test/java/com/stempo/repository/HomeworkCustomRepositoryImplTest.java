package com.stempo.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.stempo.entity.HomeworkEntity;
import com.stempo.entity.QHomeworkEntity;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class HomeworkCustomRepositoryImplTest {

    @Mock
    private JPAQueryFactory queryFactory;

    @InjectMocks
    private HomeworkCustomRepositoryImpl homeworkCustomRepository;

    private List<HomeworkEntity> dummyList;

    @BeforeEach
    void setUp() {
        HomeworkEntity dummyEntity = HomeworkEntity.builder()
            .id(1L)
            .deviceTag("tag1")
            .description("description")
            .completed(false)
            .build();
        dummyList = List.of(dummyEntity);
    }

    @Test
    void findHomeworkByDeviceTags_withNonEmptyList() {
        // given
        List<String> deviceTags = List.of("tag1", "tag2");

        @SuppressWarnings("unchecked")
        JPAQuery<HomeworkEntity> jpaQuery = mock(JPAQuery.class);
        when(queryFactory.selectFrom(any(QHomeworkEntity.class))).thenReturn(jpaQuery);
        when(jpaQuery.where(any(BooleanBuilder.class))).thenReturn(jpaQuery);
        when(jpaQuery.fetch()).thenReturn(dummyList);

        // when
        List<HomeworkEntity> result = homeworkCustomRepository.findHomeworkByDeviceTags(deviceTags);

        // then
        assertThat(result).isEqualTo(dummyList);
        verify(jpaQuery).fetch();
    }

    @Test
    void findHomeworkByDeviceTags_withEmptyList() {
        // given
        List<String> deviceTags = List.of(); // 빈 리스트

        @SuppressWarnings("unchecked")
        JPAQuery<HomeworkEntity> jpaQuery = mock(JPAQuery.class);
        when(queryFactory.selectFrom(any(QHomeworkEntity.class))).thenReturn(jpaQuery);
        when(jpaQuery.where(any(BooleanBuilder.class))).thenReturn(jpaQuery);
        when(jpaQuery.fetch()).thenReturn(dummyList);

        // when
        List<HomeworkEntity> result = homeworkCustomRepository.findHomeworkByDeviceTags(deviceTags);

        // then
        assertThat(result).isEqualTo(dummyList);
        verify(jpaQuery).fetch();
    }

    @Test
    void findHomeworkByDeviceTags_withNull() {
        // given
        List<String> deviceTags = null;

        @SuppressWarnings("unchecked")
        JPAQuery<HomeworkEntity> jpaQuery = mock(JPAQuery.class);
        when(queryFactory.selectFrom(any(QHomeworkEntity.class))).thenReturn(jpaQuery);
        when(jpaQuery.where(any(BooleanBuilder.class))).thenReturn(jpaQuery);
        when(jpaQuery.fetch()).thenReturn(dummyList);

        // when
        List<HomeworkEntity> result = homeworkCustomRepository.findHomeworkByDeviceTags(deviceTags);

        // then
        assertThat(result).isEqualTo(dummyList);
        verify(jpaQuery).fetch();
    }
}
