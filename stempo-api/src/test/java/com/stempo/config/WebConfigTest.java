package com.stempo.config;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.stempo.interceptor.ApiLoggingInterceptor;
import jakarta.servlet.ServletContext;
import java.nio.file.Path;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.resource.ResourceHttpRequestHandler;

@ExtendWith(MockitoExtension.class)
class WebConfigTest {

    @TempDir
    Path tempDir;

    @InjectMocks
    private WebConfig webConfig;

    @Mock
    private ApiLoggingInterceptor apiLoggingInterceptor;

    @Mock
    private ApplicationContext applicationContext;

    @Mock
    private ServletContext servletContext;

    private ResourceHandlerRegistry resourceHandlerRegistry;
    private String fileURL;
    private String filePath;

    @BeforeEach
    void setUp() {
        fileURL = "/files";
        filePath = tempDir.toString();
        resourceHandlerRegistry = new ResourceHandlerRegistry(applicationContext, servletContext);
    }

    @Test
    void 리소스_핸들러가_정상적으로_등록되는지_확인한다() throws Exception {
        // given
        webConfig.addResourceHandlers(resourceHandlerRegistry);

        ResourceHandlerRegistration registration = resourceHandlerRegistry.addResourceHandler(fileURL + "/**");
        registration.addResourceLocations("file://" + filePath + "/");

        ResourceHttpRequestHandler handler = spy(new ResourceHttpRequestHandler());
        UrlResource mockedResource = new UrlResource("file://" + filePath + "/");
        when(handler.getLocations()).thenReturn(List.of(mockedResource));

        // then
        List<Resource> locations = handler.getLocations();
        assertThat(locations).isNotEmpty();
        assertThat(locations).hasSize(1);
        assertThat(locations.getFirst().getURI().toString()).contains(filePath);
    }

    @Test
    void 인터셉터가_정상적으로_등록되는지_확인한다() {
        // given
        InterceptorRegistry interceptorRegistry = spy(new InterceptorRegistry());

        // when
        webConfig.addInterceptors(interceptorRegistry);

        // then
        verify(interceptorRegistry).addInterceptor(apiLoggingInterceptor);
    }
}
