package com.stempo.config;

import jakarta.validation.constraints.NotNull;
import java.io.FileNotFoundException;
import java.io.IOException;
import org.springframework.core.io.Resource;
import org.springframework.web.servlet.resource.PathResourceResolver;

public class CustomPathResourceResolver extends PathResourceResolver {

    @Override
    protected Resource getResource(@NotNull String resourcePath, @NotNull Resource location)
        throws IOException {
        Resource resource = location.createRelative(resourcePath);
        if (resource.exists() && resource.isReadable()) {
            return resource;
        }
        throw new FileNotFoundException("Resource not found: " + resourcePath);
    }
}
