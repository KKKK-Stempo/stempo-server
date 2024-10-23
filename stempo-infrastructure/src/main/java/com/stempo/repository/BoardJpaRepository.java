package com.stempo.repository;

import com.stempo.entity.BoardEntity;
import com.stempo.model.BoardCategory;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BoardJpaRepository extends JpaRepository<BoardEntity, Long> {

    Page<BoardEntity> findByCategory(BoardCategory category, Pageable pageable);

    List<BoardEntity> findByDeviceTag(String deviceTag);
}
