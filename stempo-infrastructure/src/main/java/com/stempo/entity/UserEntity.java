package com.stempo.entity;

import com.stempo.model.Role;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Table(name = "user")
public class UserEntity extends BaseEntity {

    @Id
    private String deviceTag;

    private String password;

    private int failedLoginAttempts;

    private boolean accountLocked;

    @Enumerated(EnumType.STRING)
    private Role role;
}
