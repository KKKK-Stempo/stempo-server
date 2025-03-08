package com.stempo.config;

import static org.assertj.core.api.Assertions.assertThat;

import com.stempo.logging.constants.MdcConstants;
import java.io.IOException;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.client.RestClient;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = RestClientConfig.class)
class RestClientConfigTest {

    private static MockWebServer mockWebServer;

    @Autowired
    private RestClient rhythmRestClient;

    @BeforeAll
    static void setUp() throws IOException {
        mockWebServer = new MockWebServer();
        mockWebServer.start();
    }

    @AfterAll
    static void tearDown() throws IOException {
        mockWebServer.shutdown();
    }

    @DynamicPropertySource
    static void registerProperties(DynamicPropertyRegistry registry) {
        String baseUrl = mockWebServer.url("/api").toString();
        registry.add("rhythm-generator.url", () -> baseUrl);
    }

    @AfterEach
    void clearMdc() {
        MDC.clear();
    }

    @Test
    void restClientConfig_빈이_정상적으로_생성된다() {
        // given, when
        // rhythmRestClient은 @Autowired를 통해 주입받음
        // then
        assertThat(rhythmRestClient).isNotNull();
    }

    @Test
    void rhythmRestClient_빈이_올바르게_구성되었는지_확인한다() throws Exception {
        // given
        String expectedResponseBody = "{\"message\":\"success\"}";
        mockWebServer.enqueue(new MockResponse()
            .setResponseCode(200)
            .setBody(expectedResponseBody)
            .addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE));

        // when
        ResponseEntity<String> response = rhythmRestClient.get()
            .uri("/test-endpoint")
            .retrieve()
            .toEntity(String.class);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(expectedResponseBody);

        RecordedRequest recordedRequest = mockWebServer.takeRequest();
        assertThat(recordedRequest.getMethod()).isEqualTo("GET");
        assertThat(recordedRequest.getPath()).isEqualTo("/api/test-endpoint");
        assertThat(recordedRequest.getHeader(HttpHeaders.CONTENT_TYPE)).isEqualTo(MediaType.APPLICATION_JSON_VALUE);
    }

    @Test
    void rhythmRestClient에_MdcPropagationInterceptor가_적용되어_헤더가_전파된다() throws Exception {
        // given
        // MDC에 값 설정
        MDC.put(MdcConstants.MDC_TRANSACTION_ID.getKey(), "txn-789");
        MDC.put(MdcConstants.MDC_CLIENT_IP.getKey(), "10.0.0.1");

        String expectedResponseBody = "{\"message\":\"interceptor success\"}";
        mockWebServer.enqueue(new MockResponse()
            .setResponseCode(200)
            .setBody(expectedResponseBody)
            .addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE));

        // when
        ResponseEntity<String> response = rhythmRestClient.get()
            .uri("/interceptor-test")
            .retrieve()
            .toEntity(String.class);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(expectedResponseBody);

        RecordedRequest recordedRequest = mockWebServer.takeRequest();
        // MdcPropagationInterceptor는 MDC 값이 있을 경우 헤더에 추가함
        assertThat(recordedRequest.getHeader(MdcConstants.HEADER_TRANSACTION_ID.getKey())).isEqualTo("txn-789");
        assertThat(recordedRequest.getHeader(MdcConstants.HEADER_CLIENT_IP.getKey())).isEqualTo("10.0.0.1");

        MDC.clear();
    }
}
