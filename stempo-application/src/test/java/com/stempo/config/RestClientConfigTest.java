package com.stempo.config;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
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
    private ApplicationContext applicationContext;

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

    // @DynamicPropertySource를 사용하여 rhythm-generator.url을 MockWebServer의 URL로 설정
    @DynamicPropertySource
    static void registerProperties(DynamicPropertyRegistry registry) {
        String baseUrl = mockWebServer.url("/api").toString();
        registry.add("rhythm-generator.url", () -> baseUrl);
    }

    @Test
    void restClientConfig_빈이_정상적으로_생성된다() {
        // given, when
        RestClient restClient = applicationContext.getBean(RestClient.class);

        // then
        assertThat(restClient).isNotNull();
    }

    @Test
    void rhythmRestClient_빈이_올바르게_구성되었는지_확인한다() throws Exception {
        // Given
        String expectedResponseBody = "{\"message\":\"success\"}";
        mockWebServer.enqueue(new MockResponse()
            .setResponseCode(200)
            .setBody(expectedResponseBody)
            .addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE));

        // When
        ResponseEntity<String> response = rhythmRestClient.get()
            .uri("/test-endpoint")
            .retrieve()
            .toEntity(String.class);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(expectedResponseBody);

        // 요청 검증
        okhttp3.mockwebserver.RecordedRequest recordedRequest = mockWebServer.takeRequest();
        assertThat(recordedRequest.getMethod()).isEqualTo("GET");
        assertThat(recordedRequest.getPath()).isEqualTo("/api/test-endpoint");

        // 기본 헤더 검증
        assertThat(recordedRequest.getHeader(HttpHeaders.CONTENT_TYPE)).isEqualTo(MediaType.APPLICATION_JSON_VALUE);
    }
}
