package com.stempo.util;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.stempo.dto.ApiResponse;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

class ResponseUtilsTest {

    @Test
    void 정상적으로_오류_응답을_전송한다() throws IOException {
        // given
        HttpServletResponse response = Mockito.mock(HttpServletResponse.class);
        when(response.getWriter()).thenReturn(Mockito.mock(PrintWriter.class));
        int status = HttpStatus.NOT_FOUND.value();

        // when
        ResponseUtils.sendErrorResponse(response, status);

        // then
        verify(response.getWriter()).write(ApiResponse.failure().toJson());
        verify(response).setContentType(MediaType.APPLICATION_JSON_VALUE);
        verify(response).setStatus(status);
    }
}
