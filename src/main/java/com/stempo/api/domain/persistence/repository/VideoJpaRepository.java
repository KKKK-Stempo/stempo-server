package com.stempo.api.domain.persistence.repository;

import com.stempo.api.domain.persistence.entity.VideoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface VideoJpaRepository extends JpaRepository<VideoEntity, Long> {

    @Query("SELECT v " +
            "FROM VideoEntity v " +
            "WHERE v.deleted = false " +
            "ORDER BY v.createdAt ASC")
    List<VideoEntity> findAllVideos();
}
