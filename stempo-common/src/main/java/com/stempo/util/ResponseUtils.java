package com.stempo.util;

import com.stempo.dto.ApiResponse;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

public class ResponseUtils {

    public static void sendErrorResponse(HttpServletResponse response, int status) throws IOException {
        response.getWriter().write(ApiResponse.failure().toJson());
        response.setContentType("application/json");
        response.setStatus(status);
    }
}
