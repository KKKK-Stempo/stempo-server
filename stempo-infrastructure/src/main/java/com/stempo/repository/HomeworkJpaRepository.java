package com.stempo.repository;

import com.stempo.entity.HomeworkEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface HomeworkJpaRepository extends JpaRepository<HomeworkEntity, Long> {

    Page<HomeworkEntity> findByCompleted(Boolean completed, Pageable pageable);

    List<HomeworkEntity> findByDeviceTag(String deviceTag);
}
