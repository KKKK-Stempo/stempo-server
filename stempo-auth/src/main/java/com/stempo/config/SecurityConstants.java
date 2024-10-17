package com.stempo.config;

public class SecurityConstants {

    public static final String[] PERMIT_ALL = {
            "/actuator/health",
            "/resources/files/**",
            "/favicon.ico",
            "/error",
            "/"
    };

    public static final String[] PERMIT_ALL_API_ENDPOINTS_GET = {
    };

    public static final String[] PERMIT_ALL_API_ENDPOINTS_POST = {
            "/api/v1/auth/register",
            "/api/v1/auth/login",
    };
}
