package com.stempo.config;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.FileNotFoundException;
import java.io.IOException;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.Resource;

class CustomPathResourceResolverTest {

    @Test
    void 존재하는_리소스를_반환한다() throws IOException {
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
        assertSame(relativeResource, result, "존재하고 읽을 수 있는 리소스가 반환되어야 한다.");
    }

    @Test
    void 존재하지_않는_리소스인_경우_예외를_던진다() throws IOException {
        // given
        CustomPathResourceResolver resolver = new CustomPathResourceResolver();
        String resourcePath = "nonexistent.txt";
        Resource location = mock(Resource.class);
        Resource relativeResource = mock(Resource.class);
        when(location.createRelative(resourcePath)).thenReturn(relativeResource);
        when(relativeResource.exists()).thenReturn(false);
        when(relativeResource.isReadable()).thenReturn(false);

        // when, then
        FileNotFoundException exception = assertThrows(
            FileNotFoundException.class,
            () -> resolver.getResource(resourcePath, location),
            "존재하지 않는 리소스 호출 시 FileNotFoundException이 발생해야 한다."
        );
        assertTrue(exception.getMessage().contains("Resource not found: " + resourcePath));
    }
}
