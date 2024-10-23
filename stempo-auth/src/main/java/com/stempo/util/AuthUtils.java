package com.stempo.util;

import com.stempo.exception.AuthenticationNotFoundException;
import com.stempo.exception.InvalidPrincipalException;
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
            throw new InvalidPrincipalException("인증 정보가 유효하지 않습니다. Principal이 User 타입이 아닙니다.");
        }
    }

    public static String getAuthenticationInfoDeviceTag() {
        return getAuthenticationInfo().getUsername();
    }

    private static Authentication getAuthentication() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            throw new AuthenticationNotFoundException("SecurityContext에서 인증 정보를 찾을 수 없습니다.");
        }
        if (authentication.getName() == null) {
            throw new AuthenticationNotFoundException("인증된 사용자의 이름이 없습니다.");
        }
        return authentication;
    }
}
