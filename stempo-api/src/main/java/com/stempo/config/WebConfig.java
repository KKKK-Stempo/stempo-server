package com.stempo.config;

import com.stempo.interceptor.ApiLoggingInterceptor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class WebConfig implements WebMvcConfigurer {

    private final ApiLoggingInterceptor apiLoggingInterceptor;

    @Value("${resource.file.path}")
    private String filePath;

    @Value("${resource.file.url}")
    private String fileUrl;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        log.info("Resource UploadedFile Mapped : {} -> {}", fileUrl, filePath);
        registry
            .addResourceHandler(fileUrl + "/**")
            .addResourceLocations("file://" + filePath + "/")
            .resourceChain(true)
            .addResolver(new CustomPathResourceResolver());
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(apiLoggingInterceptor);
    }
}
