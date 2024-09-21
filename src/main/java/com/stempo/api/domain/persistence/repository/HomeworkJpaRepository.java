package com.stempo.api.domain.persistence.repository;

import com.stempo.api.domain.persistence.entity.HomeworkEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HomeworkJpaRepository extends JpaRepository<HomeworkEntity, Long> {
    Page<HomeworkEntity> findByCompleted(Boolean completed, Pageable pageable);
}
