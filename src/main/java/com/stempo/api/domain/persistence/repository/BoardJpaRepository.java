package com.stempo.api.domain.persistence.repository;

import com.stempo.api.domain.domain.model.BoardCategory;
import com.stempo.api.domain.persistence.entity.BoardEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface BoardJpaRepository extends JpaRepository<BoardEntity, Long> {

    @Query("SELECT b " +
            "FROM BoardEntity b " +
            "WHERE b.category = :category " +
            "AND b.deleted = false")
    Page<BoardEntity> findByCategory(BoardCategory category, Pageable pageable);

    @Query("SELECT b " +
            "FROM BoardEntity b " +
            "WHERE b.id = :boardId " +
            "AND b.deleted = false")
    Optional<BoardEntity> findByIdAndNotDeleted(Long boardId);
}
