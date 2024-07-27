package com.stempo.api.domain.persistence.repository;

import com.stempo.api.domain.persistence.entity.RecordEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface RecordJpaRepository extends JpaRepository<RecordEntity, Long> {

    @Query("SELECT r " +
            "FROM RecordEntity r " +
            "WHERE r.createdAt BETWEEN :startDateTime AND :endDateTime " +
            "ORDER BY r.createdAt ASC")
    List<RecordEntity> findByDateBetween(
            @Param("startDateTime") LocalDateTime startDateTime,
            @Param("endDateTime") LocalDateTime endDateTime
    );

    @Query("SELECT r " +
            "FROM RecordEntity r " +
            "WHERE r.createdAt < :startDateTime " +
            "ORDER BY r.createdAt DESC " +
            "LIMIT 1")
    Optional<RecordEntity> findLatestBeforeStartDate(
            @Param("startDateTime") LocalDateTime startDateTime
    );
}
