package com.stempo.support.yaml;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.boot.env.YamlPropertySourceLoader;
import org.springframework.boot.origin.OriginTrackedValue;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import java.util.Map;

@Slf4j
public class YamlEnvironmentPostProcessor implements EnvironmentPostProcessor {
    private final YamlPropertySourceLoader loader = new YamlPropertySourceLoader();

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        String[] activeProfiles = environment.getActiveProfiles();
        DefaultResourceLoader resourceLoader = new DefaultResourceLoader();
        PathMatchingResourcePatternResolver resourcePatternResolver = new PathMatchingResourcePatternResolver(resourceLoader);

        try {
            log.info("Active Profiles: {}", (Object) activeProfiles);

            Resource[] resources = resourcePatternResolver.getResources("classpath*:application-*.yml");

            for (Resource resource : resources) {
                if (!resource.exists()) {
                    log.warn("Resource {} does not exist", resource);
                    continue;
                }

                log.info("Loading resource: {}", resource.getFilename());

                loader.load(resource.getFilename(), resource).forEach(propertySource -> {
                    Object source = propertySource.getSource();
                    if (source instanceof Map) {
                        Map<String, Object> sourceMap = (Map<String, Object>) source;
                        String profiles = extractString(sourceMap.get("spring.config.activate.on-profile"));
                        if (profiles == null) {
                            log.info("Adding propertySource: {}", propertySource.getName());
                            environment.getPropertySources().addFirst(propertySource);
                        } else {
                            log.info("Checking profiles: {}", profiles);
                            for (String profile : profiles.split(",")) {
                                if (containsProfile(activeProfiles, profile.trim())) {
                                    log.info("Profile '{}' matches, adding propertySource: {}", profile, propertySource.getName());
                                    environment.getPropertySources().addFirst(propertySource);
                                }
                            }
                        }
                    }
                });
            }
        } catch (Exception e) {
            log.error("Error loading YAML files", e);
            throw new RuntimeException(e);
        }
    }

    private String extractString(Object value) {
        if (value instanceof OriginTrackedValue) {
            return ((OriginTrackedValue) value).getValue().toString();
        }
        return value != null ? value.toString() : null;
    }

    private boolean containsProfile(String[] activeProfiles, String profile) {
        for (String activeProfile : activeProfiles) {
            if (activeProfile.equals(profile)) {
                return true;
            }
        }
        return false;
    }
}
