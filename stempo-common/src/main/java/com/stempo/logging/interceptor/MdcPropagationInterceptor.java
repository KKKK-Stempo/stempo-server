package com.stempo.logging.interceptor;

import com.stempo.logging.constants.MdcConstants;
import java.io.IOException;
import org.slf4j.MDC;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;

@Component
public class MdcPropagationInterceptor implements ClientHttpRequestInterceptor {

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution)
        throws IOException {
        // MDC에서 필요한 값들을 가져와 헤더에 추가
        String transactionId = MDC.get(MdcConstants.MDC_TRANSACTION_ID.getKey());
        String clientIp = MDC.get(MdcConstants.MDC_CLIENT_IP.getKey());

        if (transactionId != null) {
            request.getHeaders().add(MdcConstants.HEADER_TRANSACTION_ID.getKey(), transactionId);
        }
        if (clientIp != null) {
            request.getHeaders().add(MdcConstants.HEADER_CLIENT_IP.getKey(), clientIp);
        }

        return execution.execute(request, body);
    }
}
