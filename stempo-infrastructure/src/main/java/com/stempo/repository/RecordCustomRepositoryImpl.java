package com.stempo.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.stempo.entity.QRecordEntity;
import com.stempo.entity.RecordEntity;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class RecordCustomRepositoryImpl implements RecordCustomRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<RecordEntity> findRecordsByDeviceTags(List<String> deviceTags) {
        QRecordEntity qRecord = QRecordEntity.recordEntity;
        BooleanBuilder builder = new BooleanBuilder();

        if (deviceTags != null && !deviceTags.isEmpty()) {
            builder.and(qRecord.deviceTag.in(deviceTags));
        }

        return queryFactory.selectFrom(qRecord)
            .where(builder)
            .fetch();
    }

    @Override
    public List<RecordEntity> findRecordsByDeviceTagsAndDateRange(
        List<String> deviceTags, LocalDate startDate, LocalDate endDate) {

        QRecordEntity qRecord = QRecordEntity.recordEntity;
        BooleanBuilder builder = new BooleanBuilder();

        if (deviceTags != null && !deviceTags.isEmpty()) {
            builder.and(qRecord.deviceTag.in(deviceTags));
        }

        if (startDate != null && endDate != null) {
            // 시작일은 자정부터, 종료일은 해당일의 끝까지 포함하도록 처리
            LocalDateTime startDateTime = startDate.atStartOfDay();
            LocalDateTime endDateTime = endDate.atStartOfDay().plusDays(1);
            builder.and(qRecord.createdAt.between(startDateTime, endDateTime));
        }

        return queryFactory.selectFrom(qRecord)
            .where(builder)
            .fetch();
    }
}
