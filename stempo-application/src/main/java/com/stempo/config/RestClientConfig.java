package com.stempo.config;

import com.stempo.logging.interceptor.MdcPropagationInterceptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestClient;

@Configuration
public class RestClientConfig {

    @Bean
    public RestClient rhythmRestClient(
        @Value("${rhythm-generator.url}") String rhythmGeneratorUrl
    ) {
        return RestClient.builder()
            .baseUrl(rhythmGeneratorUrl)
            .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .requestInterceptor(new MdcPropagationInterceptor())
            .build();
    }
}
