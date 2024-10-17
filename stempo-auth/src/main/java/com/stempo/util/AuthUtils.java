package com.stempo.util;

import com.stempo.exception.AuthenticationInfoNotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;

public class AuthUtils {

    public static User getAuthenticationInfo() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication.getName() == null) {
            throw new AuthenticationInfoNotFoundException();
        }
        if (authentication.getPrincipal() instanceof User) {
            return (User) authentication.getPrincipal();
        } else {
            throw new AuthenticationInfoNotFoundException("인증 정보가 존재하지 않습니다.");
        }
    }

    public static String getAuthenticationInfoDeviceTag() {
        return getAuthenticationInfo().getUsername();
    }
}
