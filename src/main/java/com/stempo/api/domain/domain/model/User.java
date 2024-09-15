package com.stempo.api.domain.domain.model;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class User implements UserDetails {

    private String deviceTag;
    private String password;
    private Role role;

    public static User create(String deviceTag, String password) {
        return new User(deviceTag, password, Role.USER);
    }

    public static User createUserDetails(User user) {
        return User.builder()
                .deviceTag(user.getDeviceTag())
                .password(user.getPassword())
                .role(user.getRole())
                .build();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(() -> role.name());
    }

    @Override
    public String getUsername() {
        return deviceTag;
    }

    public boolean isAdmin() {
        return role.equals(Role.ADMIN);
    }
}
