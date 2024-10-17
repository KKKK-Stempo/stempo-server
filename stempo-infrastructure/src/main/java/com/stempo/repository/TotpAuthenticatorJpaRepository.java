package com.stempo.repository;

import com.stempo.entity.TotpAuthenticatorEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TotpAuthenticatorJpaRepository extends JpaRepository<TotpAuthenticatorEntity, String> {
}
