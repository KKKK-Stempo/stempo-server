package com.stempo.repository;

import com.stempo.model.Homework;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface HomeworkRepository {

    Homework save(Homework homework);

    void delete(Homework homework);

    void deleteAll(List<Homework> homeworks);

    Page<Homework> findByCompleted(Boolean completed, Pageable pageable);

    Homework findByIdOrThrow(Long homeworkId);

    List<Homework> findByDeviceTag(String deviceTag);
}
