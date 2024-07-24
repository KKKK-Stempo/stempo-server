package com.stempo.api.domain.persistence.repository;

import com.stempo.api.domain.persistence.entity.ArticleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ArticleJpaRepository extends JpaRepository<ArticleEntity, Long> {

    @Query("SELECT a " +
            "FROM ArticleEntity a " +
            "WHERE a.deleted = false " +
            "ORDER BY a.createdAt ASC")
    List<ArticleEntity> findAllArticles();

    @Query("SELECT a " +
            "FROM ArticleEntity a " +
            "WHERE a.id = :articleId AND a.deleted = false")
    Optional<ArticleEntity> findByIdAndNotDeleted(Long articleId);
}
