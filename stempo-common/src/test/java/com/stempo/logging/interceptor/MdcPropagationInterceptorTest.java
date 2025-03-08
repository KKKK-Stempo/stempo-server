package com.stempo.logging.interceptor;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.stempo.logging.constants.MdcConstants;
import java.io.IOException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.slf4j.MDC;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpResponse;

class MdcPropagationInterceptorTest {

    private final MdcPropagationInterceptor interceptor = new MdcPropagationInterceptor();

    @AfterEach
    void tearDown() {
        MDC.clear();
    }

    @Test
    void 헤더가_MDC값으로_정상적으로_추가된다() throws IOException {
        // given
        MDC.put(MdcConstants.MDC_TRANSACTION_ID.getKey(), "txn-test");
        MDC.put(MdcConstants.MDC_CLIENT_IP.getKey(), "192.168.1.1");

        HttpHeaders headers = new HttpHeaders();
        HttpRequest request = mock(HttpRequest.class);
        when(request.getHeaders()).thenReturn(headers);

        byte[] body = new byte[0];
        ClientHttpRequestExecution execution = mock(ClientHttpRequestExecution.class);
        ClientHttpResponse dummyResponse = mock(ClientHttpResponse.class);
        when(execution.execute(request, body)).thenReturn(dummyResponse);

        // when
        ClientHttpResponse response = interceptor.intercept(request, body, execution);

        // then
        assertThat(headers.getFirst(MdcConstants.HEADER_TRANSACTION_ID.getKey())).isEqualTo("txn-test");
        assertThat(headers.getFirst(MdcConstants.HEADER_CLIENT_IP.getKey())).isEqualTo("192.168.1.1");
        assertThat(response).isEqualTo(dummyResponse);
    }

    @Test
    void MDC값이_없으면_헤더는_추가되지_않는다() throws IOException {
        // given
        MDC.clear();

        HttpHeaders headers = new HttpHeaders();
        HttpRequest request = mock(HttpRequest.class);
        when(request.getHeaders()).thenReturn(headers);

        byte[] body = new byte[0];
        ClientHttpRequestExecution execution = mock(ClientHttpRequestExecution.class);
        ClientHttpResponse dummyResponse = mock(ClientHttpResponse.class);
        when(execution.execute(request, body)).thenReturn(dummyResponse);

        // when
        ClientHttpResponse response = interceptor.intercept(request, body, execution);

        // then
        assertThat(headers.containsKey(MdcConstants.HEADER_TRANSACTION_ID.getKey())).isFalse();
        assertThat(headers.containsKey(MdcConstants.HEADER_CLIENT_IP.getKey())).isFalse();
        assertThat(response).isEqualTo(dummyResponse);
    }
}
