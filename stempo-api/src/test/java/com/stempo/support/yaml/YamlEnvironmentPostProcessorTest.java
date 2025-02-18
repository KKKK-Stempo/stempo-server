package com.stempo.support.yaml;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.Collections;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.YamlPropertySourceLoader;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class YamlEnvironmentPostProcessorTest {

    @Mock
    private ConfigurableEnvironment environment;

    @Mock
    private SpringApplication application;

    @Mock
    private ResourcePatternResolver resourcePatternResolver;

    @Mock
    private YamlPropertySourceLoader yamlPropertySourceLoader;

    private MutablePropertySources propertySources;

    private YamlEnvironmentPostProcessor yamlEnvironmentPostProcessor;

    @BeforeEach
    void setUp() {
        yamlEnvironmentPostProcessor = new YamlEnvironmentPostProcessor(resourcePatternResolver);

        // 활성 프로파일 설정
        String[] activeProfiles = new String[]{"test"};
        when(environment.getActiveProfiles()).thenReturn(activeProfiles);

        // 로더 설정
        ReflectionTestUtils.setField(yamlEnvironmentPostProcessor, "loader", yamlPropertySourceLoader);

        // 모의 MutablePropertySources 설정
        propertySources = spy(new MutablePropertySources());
        lenient().when(environment.getPropertySources()).thenReturn(propertySources);
    }

    @Test
    void 프로파일이_적용된_YAML_리소스를_로드한다() throws IOException {
        // given
        Resource resource = mock(Resource.class);
        when(resource.exists()).thenReturn(true);
        when(resource.getFilename()).thenReturn("application-test.yml");

        when(resourcePatternResolver.getResources(anyString())).thenReturn(new Resource[]{resource});

        PropertySource<?> propertySource = new PropertySource<>("application-test.yml") {
            private final Map<String, Object> source = Map.of("some.key", "some.value");

            @Override
            public Object getProperty(String name) {
                return source.get(name);
            }

            @Override
            public Object getSource() {
                return source;
            }
        };

        when(yamlPropertySourceLoader.load(anyString(), any(Resource.class)))
            .thenReturn(Collections.singletonList(propertySource));

        // when
        yamlEnvironmentPostProcessor.postProcessEnvironment(environment, application);

        // then
        verify(propertySources, atLeastOnce()).addFirst(propertySource);
    }

    @Test
    void 존재하지_않는_리소스를_로드_하지_않는다() throws IOException {
        // given
        when(resourcePatternResolver.getResources(anyString())).thenReturn(new Resource[0]);

        // when
        yamlEnvironmentPostProcessor.postProcessEnvironment(environment, application);

        // then
        verify(propertySources, never()).addFirst(any(PropertySource.class));
    }

    @Test
    void 프로파일_매칭시_프로퍼티소스가_추가된다() throws IOException {
        // activeProfiles: ["test"]
        Resource resource = mock(Resource.class);
        when(resource.exists()).thenReturn(true);
        when(resource.getFilename()).thenReturn("application-profile.yml");
        when(resourcePatternResolver.getResources(anyString())).thenReturn(new Resource[]{resource});

        // source에 PROFILE_KEY가 존재하며, 값이 "test"로 되어 있음.
        Map<String, Object> sourceMap = Map.of("spring.config.activate.on-profile", "test", "other.key", "value");
        PropertySource<?> propertySource = new PropertySource<>("application-profile.yml") {
            @Override
            public Object getProperty(String name) {
                return sourceMap.get(name);
            }

            @Override
            public Object getSource() {
                return sourceMap;
            }
        };

        when(yamlPropertySourceLoader.load("application-profile.yml", resource))
            .thenReturn(Collections.singletonList(propertySource));

        // when
        yamlEnvironmentPostProcessor.postProcessEnvironment(environment, application);

        // then : matching profile "test" 있으므로 추가되어야 함.
        verify(propertySources, atLeastOnce()).addFirst(propertySource);
    }

    @Test
    void 프로파일_비매칭시_프로퍼티소스가_추가되지_않는다() throws IOException {
        // activeProfiles: ["test"]
        Resource resource = mock(Resource.class);
        when(resource.exists()).thenReturn(true);
        when(resource.getFilename()).thenReturn("application-profile.yml");
        when(resourcePatternResolver.getResources(anyString())).thenReturn(new Resource[]{resource});

        // source에 PROFILE_KEY가 존재하지만, 값이 "prod" (활성 프로파일에 없음)
        Map<String, Object> sourceMap = Map.of("spring.config.activate.on-profile", "prod", "other.key", "value");
        PropertySource<?> propertySource = new PropertySource<>("application-profile.yml") {
            @Override
            public Object getProperty(String name) {
                return sourceMap.get(name);
            }

            @Override
            public Object getSource() {
                return sourceMap;
            }
        };

        when(yamlPropertySourceLoader.load("application-profile.yml", resource))
            .thenReturn(Collections.singletonList(propertySource));

        // when
        yamlEnvironmentPostProcessor.postProcessEnvironment(environment, application);

        // then: 활성 프로파일이 "test"이므로 "prod"와 매칭되지 않아 추가되지 않아야 함.
        verify(propertySources, never()).addFirst(propertySource);
    }

    @Test
    void 프로퍼티_소스가_Map이_아닌경우_추가되지_않는다() throws IOException {
        // activeProfiles: ["test"]
        Resource resource = mock(Resource.class);
        when(resource.exists()).thenReturn(true);
        when(resource.getFilename()).thenReturn("application-invalid.yml");
        when(resourcePatternResolver.getResources(anyString())).thenReturn(new Resource[]{resource});

        // PropertySource의 source가 Map이 아닌 경우 (예: 단순 문자열)
        PropertySource<?> propertySource = new PropertySource<>("application-invalid.yml") {
            @Override
            public Object getProperty(String name) {
                return null;
            }

            @Override
            public Object getSource() {
                return "Not a map";
            }
        };

        when(yamlPropertySourceLoader.load("application-invalid.yml", resource))
            .thenReturn(Collections.singletonList(propertySource));

        // when
        yamlEnvironmentPostProcessor.postProcessEnvironment(environment, application);

        // then
        verify(propertySources, never()).addFirst(propertySource);
    }
}
