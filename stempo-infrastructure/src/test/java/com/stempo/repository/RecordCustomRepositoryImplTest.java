package com.stempo.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.stempo.entity.QRecordEntity;
import com.stempo.entity.RecordEntity;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class RecordCustomRepositoryImplTest {

    @Mock
    private JPAQueryFactory queryFactory;

    @InjectMocks
    private RecordCustomRepositoryImpl recordCustomRepository;

    private List<RecordEntity> dummyList;

    @BeforeEach
    void setUp() {
        RecordEntity dummyEntity = RecordEntity.builder()
            .id(1L)
            .deviceTag("device123")
            .accuracy("90")
            .duration("10")
            .steps("1000")
            .leftFootAverageSpeed("1.0")
            .rightFootAverageSpeed("1.0")
            .bit("1")
            .bpm("60")
            .build();
        dummyList = List.of(dummyEntity);
    }

    @Test
    void findRecordsByDeviceTags_비어있지_않은_목록을_입력하면_레코드를_조회한다() {
        // given
        List<String> deviceTags = List.of("device123");

        @SuppressWarnings("unchecked")
        JPAQuery<RecordEntity> jpaQuery = mock(JPAQuery.class);
        when(queryFactory.selectFrom(any(QRecordEntity.class))).thenReturn(jpaQuery);
        when(jpaQuery.where(any(BooleanBuilder.class))).thenReturn(jpaQuery);
        when(jpaQuery.fetch()).thenReturn(dummyList);

        // when
        List<RecordEntity> result = recordCustomRepository.findRecordsByDeviceTags(deviceTags);

        // then
        assertThat(result).isEqualTo(dummyList);
        verify(jpaQuery).fetch();
    }

    @Test
    void findRecordsByDeviceTags_빈_목록을_입력하면_전체_레코드를_조회한다() {
        // given
        List<String> deviceTags = List.of(); // 빈 리스트

        @SuppressWarnings("unchecked")
        JPAQuery<RecordEntity> jpaQuery = mock(JPAQuery.class);
        when(queryFactory.selectFrom(any(QRecordEntity.class))).thenReturn(jpaQuery);
        when(jpaQuery.where(any(BooleanBuilder.class))).thenReturn(jpaQuery);
        when(jpaQuery.fetch()).thenReturn(dummyList);

        // when
        List<RecordEntity> result = recordCustomRepository.findRecordsByDeviceTags(deviceTags);

        // then
        assertThat(result).isEqualTo(dummyList);
        verify(jpaQuery).fetch();
    }

    @Test
    void findRecordsByDeviceTagsAndDateRange_정상_파라미터로_조회한다() {
        // given
        List<String> deviceTags = List.of("device123");
        LocalDate startDate = LocalDate.of(2024, 1, 1);
        LocalDate endDate = LocalDate.of(2024, 1, 2);

        @SuppressWarnings("unchecked")
        JPAQuery<RecordEntity> jpaQuery = mock(JPAQuery.class);
        when(queryFactory.selectFrom(any(QRecordEntity.class))).thenReturn(jpaQuery);
        when(jpaQuery.where(any(BooleanBuilder.class))).thenReturn(jpaQuery);
        when(jpaQuery.fetch()).thenReturn(dummyList);

        // when
        List<RecordEntity> result = recordCustomRepository.findRecordsByDeviceTagsAndDateRange(deviceTags, startDate,
            endDate);

        // then
        assertThat(result).isEqualTo(dummyList);
        verify(jpaQuery).fetch();
    }

    @Test
    void findRecordsByDeviceTagsAndDateRange_날짜_파라미터가_null이면_레코드를_조회한다() {
        // given
        List<String> deviceTags = List.of("device123");
        LocalDate startDate = null;
        LocalDate endDate = null;

        @SuppressWarnings("unchecked")
        JPAQuery<RecordEntity> jpaQuery = mock(JPAQuery.class);
        when(queryFactory.selectFrom(any(QRecordEntity.class))).thenReturn(jpaQuery);
        when(jpaQuery.where(any(BooleanBuilder.class))).thenReturn(jpaQuery);
        when(jpaQuery.fetch()).thenReturn(dummyList);

        // when
        List<RecordEntity> result =
            recordCustomRepository.findRecordsByDeviceTagsAndDateRange(deviceTags, startDate, endDate);

        // then
        assertThat(result).isEqualTo(dummyList);
        verify(jpaQuery).fetch();
    }
}
