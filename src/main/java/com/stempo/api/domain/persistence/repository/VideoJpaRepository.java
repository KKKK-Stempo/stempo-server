package com.stempo.api.domain.persistence.repository;

import com.stempo.api.domain.persistence.entity.VideoEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface VideoJpaRepository extends JpaRepository<VideoEntity, Long> {

    @Query("SELECT v " +
            "FROM VideoEntity v " +
            "WHERE v.deleted = false")
    Page<VideoEntity> findAllVideos(Pageable pageable);

    @Query("SELECT v " +
            "FROM VideoEntity v " +
            "WHERE v.id = :videoId AND v.deleted = false")
    Optional<VideoEntity> findByIdAndNotDeleted(Long videoId);
}
