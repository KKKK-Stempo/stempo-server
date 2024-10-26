package com.stempo.util;

import com.stempo.exception.BaseException;
import com.stempo.exception.ErrorCode;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;

public class AuthUtils {

    public static User getAuthenticationInfo() {
        Authentication authentication = getAuthentication();
        Object principal = authentication.getPrincipal();

        if (principal instanceof User) {
            return (User) principal;
        } else {
            throw new BaseException(ErrorCode.INVALID_PRINCIPAL);
        }
    }

    public static String getAuthenticationInfoDeviceTag() {
        return getAuthenticationInfo().getUsername();
    }

    private static Authentication getAuthentication() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            throw new BaseException(ErrorCode.AUTHENTICATION_NOT_FOUND);
        }
        if (authentication.getName() == null) {
            throw new BaseException(ErrorCode.AUTHENTICATION_NOT_FOUND, "인증된 사용자의 이름이 없습니다.");
        }
        return authentication;
    }
}
