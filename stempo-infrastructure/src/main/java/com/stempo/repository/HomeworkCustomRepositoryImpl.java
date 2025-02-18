package com.stempo.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.stempo.entity.HomeworkEntity;
import com.stempo.entity.QHomeworkEntity;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class HomeworkCustomRepositoryImpl implements HomeworkCustomRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<HomeworkEntity> findHomeworkByDeviceTags(List<String> deviceTags) {
        QHomeworkEntity qHomework = QHomeworkEntity.homeworkEntity;
        BooleanBuilder builder = new BooleanBuilder();

        if (deviceTags != null && !deviceTags.isEmpty()) {
            builder.and(qHomework.deviceTag.in(deviceTags));
        }

        return queryFactory.selectFrom(qHomework)
            .where(builder)
            .fetch();
    }
}
