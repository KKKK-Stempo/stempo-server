package com.stempo.api.global.util;

import com.stempo.api.global.dto.ApiResponse;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

public class ResponseUtil {

    public static void sendErrorResponse(HttpServletResponse response, int status) throws IOException {
        response.getWriter().write(ApiResponse.failure().toJson());
        response.setContentType("application/json");
        response.setStatus(status);
    }
}
