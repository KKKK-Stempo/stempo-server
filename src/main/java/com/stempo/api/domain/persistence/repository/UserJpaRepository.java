package com.stempo.api.domain.persistence.repository;

import com.stempo.api.domain.persistence.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserJpaRepository extends JpaRepository<UserEntity, String> {
}
