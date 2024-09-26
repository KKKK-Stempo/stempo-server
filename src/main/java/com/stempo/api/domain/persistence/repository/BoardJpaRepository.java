package com.stempo.api.domain.persistence.repository;

import com.stempo.api.domain.domain.model.BoardCategory;
import com.stempo.api.domain.persistence.entity.BoardEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BoardJpaRepository extends JpaRepository<BoardEntity, Long> {

    Page<BoardEntity> findByCategory(BoardCategory category, Pageable pageable);

    List<BoardEntity> findByDeviceTag(String deviceTag);
}
