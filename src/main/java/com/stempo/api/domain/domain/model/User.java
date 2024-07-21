package com.stempo.api.domain.domain.model;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

@Getter
@Setter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class User implements UserDetails {

    private String id;
    private String password;
    private Role role;

    public static User createUserDetails(User user) {
        return new User(user.getId(), user.getPassword(), user.getRole());
    }

    public static User create(String deviceTag) {
        return new User(deviceTag, "", Role.USER);
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(new SimpleGrantedAuthority(getRole().getKey()));
    }

    @Override
    public String getUsername() {
        return id;
    }

    public void updatePassword(String encodedPassword) {
        setPassword(encodedPassword);
    }
}
