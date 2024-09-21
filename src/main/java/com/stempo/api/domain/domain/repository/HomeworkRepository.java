package com.stempo.api.domain.domain.repository;

import com.stempo.api.domain.domain.model.Homework;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface HomeworkRepository {

    Homework save(Homework homework);

    Page<Homework> findByCompleted(Boolean completed, Pageable pageable);

    Homework findByIdOrThrow(Long homeworkId);

    void delete(Homework homework);
}
