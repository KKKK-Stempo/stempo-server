package com.stempo.util;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

class HttpReqResUtilsTest {

    private HttpServletRequest mockRequest;

    @BeforeEach
    void setUp() {
        mockRequest = Mockito.mock(HttpServletRequest.class);
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(mockRequest));
    }

    @Test
    void 클라이언트_IP_헤더에서_정상적으로_가져온다() {
        // given
        String expectedIp = "192.168.1.1";
        when(mockRequest.getHeader("X-Forwarded-For")).thenReturn(expectedIp);

        // when
        String clientIp = HttpReqResUtils.getClientIpAddressIfServletRequestExist();

        // then
        assertThat(clientIp).isEqualTo(expectedIp);
    }

    @Test
    void 여러_IP_헤더가_존재할_경우_첫번째_IP를_가져온다() {
        // given
        String expectedIp = "192.168.1.1, 10.0.0.1";
        when(mockRequest.getHeader("X-Forwarded-For")).thenReturn(expectedIp);

        // when
        String clientIp = HttpReqResUtils.getClientIpAddressIfServletRequestExist();

        // then
        assertThat(clientIp).isEqualTo("192.168.1.1");
    }

    @Test
    void 클라이언트_IP_헤더가_없을_경우_remoteAddr을_가져온다() {
        // given
        String expectedIp = "172.16.0.1";
        when(mockRequest.getRemoteAddr()).thenReturn(expectedIp);

        // when
        String clientIp = HttpReqResUtils.getClientIpAddressIfServletRequestExist();

        // then
        assertThat(clientIp).isEqualTo(expectedIp);
    }

    @Test
    void 요청이_없을_경우_기본_IP를_반환한다() {
        // given
        RequestContextHolder.setRequestAttributes(null);

        // when
        String clientIp = HttpReqResUtils.getClientIpAddressIfServletRequestExist();

        // then
        assertThat(clientIp).isEqualTo("0.0.0.0");
    }

    @Test
    void IP_헤더가_존재하지_않고_모든_헤더가_unknown일_경우_remoteAddr을_가져온다() {
        // given
        when(mockRequest.getHeader("X-Forwarded-For")).thenReturn("unknown");
        when(mockRequest.getRemoteAddr()).thenReturn("10.0.0.2");

        // when
        String clientIp = HttpReqResUtils.getClientIpAddressIfServletRequestExist();

        // then
        assertThat(clientIp).isEqualTo("10.0.0.2");
    }
}
