package com.stempo.api.domain.persistence.repository;

import com.stempo.api.domain.domain.model.BoardCategory;
import com.stempo.api.domain.persistence.entity.BoardEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface BoardJpaRepository extends JpaRepository<BoardEntity, Long> {

    @Query("SELECT b " +
            "FROM BoardEntity b " +
            "WHERE b.category = :category " +
            "AND b.deleted = false")
    List<BoardEntity> findByCategory(BoardCategory category);

    @Query("SELECT b " +
            "FROM BoardEntity b " +
            "WHERE b.id = :boardId " +
            "AND b.deleted = false")
    Optional<BoardEntity> findByIdAndNotDeleted(Long boardId);
}