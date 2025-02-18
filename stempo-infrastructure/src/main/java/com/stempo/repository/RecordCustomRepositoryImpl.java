package com.stempo.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.stempo.entity.QRecordEntity;
import com.stempo.entity.RecordEntity;
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
}
