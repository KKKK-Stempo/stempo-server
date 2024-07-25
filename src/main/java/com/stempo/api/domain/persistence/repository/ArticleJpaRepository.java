package com.stempo.api.domain.persistence.repository;

import com.stempo.api.domain.persistence.entity.ArticleEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface ArticleJpaRepository extends JpaRepository<ArticleEntity, Long> {

    @Query("SELECT a " +
            "FROM ArticleEntity a " +
            "WHERE a.deleted = false")
    Page<ArticleEntity> findAllArticles(Pageable pageable);

    @Query("SELECT a " +
            "FROM ArticleEntity a " +
            "WHERE a.id = :articleId AND a.deleted = false")
    Optional<ArticleEntity> findByIdAndNotDeleted(Long articleId);
}
