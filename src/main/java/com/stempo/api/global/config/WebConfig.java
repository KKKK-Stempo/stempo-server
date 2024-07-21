package com.stempo.api.global.config;

import com.stempo.api.global.handler.ApiLoggingInterceptor;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.resource.PathResourceResolver;

import java.io.FileNotFoundException;
import java.io.IOException;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class WebConfig implements WebMvcConfigurer {

    private final ApiLoggingInterceptor apiLoggingInterceptor;

    @Value("${resource.file.path}")
    private String filePath;

    @Value("${resource.file.url}")
    private String fileURL;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        log.info("Resource UploadedFile Mapped : {} -> {}", fileURL, filePath);
        registry
                .addResourceHandler(fileURL + "/**")
                .addResourceLocations("file://" + filePath + "/")
                .resourceChain(true)
                .addResolver(new PathResourceResolver() {
                    @Override
                    protected Resource getResource(@NotNull String resourcePath, @NotNull Resource location) throws IOException {
                        Resource resource = location.createRelative(resourcePath);
                        if (resource.exists() && resource.isReadable()) {
                            return resource;
                        }
                        throw new FileNotFoundException("Resource not found: " + resourcePath);
                    }
                });
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(apiLoggingInterceptor);
    }
}
