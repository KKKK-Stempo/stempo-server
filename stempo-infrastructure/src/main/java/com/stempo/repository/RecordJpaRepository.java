package com.stempo.repository;

import com.stempo.entity.RecordEntity;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface RecordJpaRepository extends JpaRepository<RecordEntity, Long> {

    @Query("SELECT r " +
            "FROM RecordEntity r " +
            "WHERE r.createdAt BETWEEN :startDateTime AND :endDateTime " +
            "AND r.deviceTag = :deviceTag " +
            "ORDER BY r.createdAt ASC")
    List<RecordEntity> findByDateBetween(
            @Param("deviceTag") String deviceTag,
            @Param("startDateTime") LocalDateTime startDateTime,
            @Param("endDateTime") LocalDateTime endDateTime
    );

    @Query("SELECT r " +
            "FROM RecordEntity r " +
            "WHERE r.createdAt < :startDateTime " +
            "AND r.deviceTag = :deviceTag " +
            "ORDER BY r.createdAt DESC " +
            "LIMIT 1")
    Optional<RecordEntity> findLatestBeforeStartDate(
            @Param("deviceTag") String deviceTag,
            @Param("startDateTime") LocalDateTime startDateTime
    );

    List<RecordEntity> findByDeviceTag(String deviceTag);

    @Query("SELECT r.createdAt " +
            "FROM RecordEntity r " +
            "WHERE r.deviceTag = :deviceTag " +
            "ORDER BY r.createdAt DESC")
    List<LocalDateTime> findCreatedAtByDeviceTagOrderByCreatedAtDesc(
            @Param("deviceTag") String deviceTag
    );

    int countByDeviceTagAndCreatedAtBetween(String deviceTag, LocalDateTime startDateTime, LocalDateTime endDateTime);
}
