package com.stempo.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Role {

    USER("ROLE_USER", "Normal User"),
    ADMIN("ROLE_ADMIN", "Administrator");

    private final String key;
    private final String description;
}
