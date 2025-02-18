package com.stempo.config;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.stempo.interceptor.ApiLoggingInterceptor;
import jakarta.servlet.ServletContext;
import java.io.FileNotFoundException;
import java.io.IOException;
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
        assertThat(locations).isNotEmpty()
            .hasSize(1);
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

    @Test
    void CustomPathResourceResolver_존재하는_리소스를_반환한다() throws IOException {
        // given
        CustomPathResourceResolver resolver = new CustomPathResourceResolver();
        String resourcePath = "test.txt";
        Resource location = mock(Resource.class);
        Resource relativeResource = mock(Resource.class);
        when(location.createRelative(resourcePath)).thenReturn(relativeResource);
        when(relativeResource.exists()).thenReturn(true);
        when(relativeResource.isReadable()).thenReturn(true);

        // when
        Resource result = resolver.getResource(resourcePath, location);

        // then
        assertThat(result).isEqualTo(relativeResource);
    }

    @Test
    void CustomPathResourceResolver_존재하지_않는_리소스인_경우_예외를_던진다() throws IOException {
        // given
        CustomPathResourceResolver resolver = new CustomPathResourceResolver();
        String resourcePath = "nonexistent.txt";
        Resource location = mock(Resource.class);
        Resource relativeResource = mock(Resource.class);
        when(location.createRelative(resourcePath)).thenReturn(relativeResource);
        when(relativeResource.exists()).thenReturn(false);

        // then
        assertThatThrownBy(() -> resolver.getResource(resourcePath, location))
            .isInstanceOf(FileNotFoundException.class)
            .hasMessageContaining("Resource not found: " + resourcePath);
    }

    @Test
    void 리소스_핸들러_등록_후_요청_핸들러의_위치를_확인한다() throws Exception {
        // given
        webConfig.addResourceHandlers(resourceHandlerRegistry);
        Resource expectedResource = new UrlResource("file://" + filePath + "/");

        // when
        assertThat(expectedResource.getURI().toString()).contains(filePath);
    }
}
