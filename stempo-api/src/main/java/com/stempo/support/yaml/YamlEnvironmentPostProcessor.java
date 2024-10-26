package com.stempo.support.yaml;

import java.util.Map;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.boot.env.YamlPropertySourceLoader;
import org.springframework.boot.origin.OriginTrackedValue;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;

@Slf4j
public class YamlEnvironmentPostProcessor implements EnvironmentPostProcessor {

    private static final String YAML_PATTERN = "classpath*:application-*.yml";
    private static final String PROFILE_KEY = "spring.config.activate.on-profile";

    private final YamlPropertySourceLoader loader = new YamlPropertySourceLoader();

    @Getter
    private final ResourcePatternResolver resourcePatternResolver;

    public YamlEnvironmentPostProcessor() {
        this.resourcePatternResolver = new PathMatchingResourcePatternResolver();
    }

    public YamlEnvironmentPostProcessor(ResourcePatternResolver resourcePatternResolver) {
        this.resourcePatternResolver = resourcePatternResolver;
    }

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        String[] activeProfiles = environment.getActiveProfiles();
        log.info("Active Profiles: {}", (Object) activeProfiles);

        try {
            loadYamlResources(environment, activeProfiles);
        } catch (Exception e) {
            log.error("Error loading YAML files", e);
            throw new RuntimeException("Failed to load YAML files", e);
        }
    }

    private void loadYamlResources(ConfigurableEnvironment environment, String[] activeProfiles) throws Exception {
        Resource[] resources = resourcePatternResolver.getResources(YAML_PATTERN);

        for (Resource resource : resources) {
            if (resource.exists()) {
                log.info("Loading resource: {}", resource.getFilename());
                try {
                    loadPropertySources(environment, resource, activeProfiles);
                } catch (Exception e) {
                    log.error("Failed to load property sources from resource: {}", resource.getFilename(), e);
                }
            } else {
                log.warn("Resource {} does not exist", resource);
            }
        }
    }

    private void loadPropertySources(ConfigurableEnvironment environment, Resource resource, String[] activeProfiles)
            throws Exception {
        loader.load(resource.getFilename(), resource).forEach(propertySource -> {
            Object source = propertySource.getSource();

            if (source instanceof Map) {
                Map<String, Object> sourceMap = (Map<String, Object>) source;
                String profiles = extractString(sourceMap.get(PROFILE_KEY));

                if (profiles == null) {
                    log.info("Adding propertySource without profile check: {}", propertySource.getName());
                    environment.getPropertySources().addFirst(propertySource);
                } else {
                    addPropertySourceIfProfileMatches(environment, propertySource, profiles, activeProfiles);
                }
            } else {
                log.warn("Property source {} is not a Map. Skipping this source.", propertySource.getName());
            }
        });
    }

    private void addPropertySourceIfProfileMatches(ConfigurableEnvironment environment,
            org.springframework.core.env.PropertySource<?> propertySource,
            String profiles,
            String[] activeProfiles) {
        for (String profile : profiles.split(",")) {
            if (containsProfile(activeProfiles, profile.trim())) {
                log.info("Profile '{}' matches, adding propertySource: {}", profile, propertySource.getName());
                environment.getPropertySources().addFirst(propertySource);
                break; // 매칭되는 프로파일이 있으면 추가 후 바로 종료
            }
        }
    }

    private String extractString(Object value) {
        if (value == null) {
            return null;
        }
        return value instanceof OriginTrackedValue ? ((OriginTrackedValue) value).getValue().toString()
                : value.toString();
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
