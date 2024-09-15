package com.stempo.api.domain.domain.model;

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
    private Role role;

    public static User create(String deviceTag, String password) {
        return new User(deviceTag, password, Role.USER);
    }

    public boolean isAdmin() {
        return role.equals(Role.ADMIN);
    }
}
