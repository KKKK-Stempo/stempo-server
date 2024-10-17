package com.stempo.model;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class User {

    private String deviceTag;
    private String password;
    private int failedLoginAttempts;
    private boolean accountLocked;
    private Role role;

    public static User create(String deviceTag, String password) {
        return new User(deviceTag, password, 0, false, Role.USER);
    }

    public void incrementFailedLoginAttempts() {
        this.failedLoginAttempts++;
    }

    public void resetFailedLoginAttempts() {
        this.failedLoginAttempts = 0;
        this.accountLocked = false;
    }

    public void lockAccount() {
        this.accountLocked = true;
    }

    public boolean isAdmin() {
        return role.equals(Role.ADMIN);
    }
}
