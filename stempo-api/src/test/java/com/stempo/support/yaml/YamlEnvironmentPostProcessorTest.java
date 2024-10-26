package com.stempo.support.yaml;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyString;
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
import org.mockito.InjectMocks;
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

    @InjectMocks
    private YamlEnvironmentPostProcessor yamlEnvironmentPostProcessor;

    private MutablePropertySources propertySources;

    @BeforeEach
    void setUp() {
        // Active profile 설정
        String[] activeProfiles = new String[]{"test"};
        when(environment.getActiveProfiles()).thenReturn(activeProfiles);

        // YamlPropertySourceLoader 설정
        ReflectionTestUtils.setField(yamlEnvironmentPostProcessor, "loader", yamlPropertySourceLoader);

        // Mock MutablePropertySources 설정
        propertySources = spy(new MutablePropertySources());
    }

    @Test
    void 프로파일이_적용된_YAML_리소스를_로드한다() throws IOException {
        // given
        Resource resource = mock(Resource.class);

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

        MutablePropertySources propertySources = new MutablePropertySources();
        when(environment.getPropertySources()).thenReturn(propertySources);

        // when
        yamlEnvironmentPostProcessor.postProcessEnvironment(environment, application);

        // then
        assertThat(propertySources.contains("application-test.yml")).isTrue();
    }

    @Test
    void 존재하지_않는_리소스를_로드_하지_않는다() {
        // given
        Resource resource = mock(Resource.class);

        // when
        yamlEnvironmentPostProcessor.postProcessEnvironment(environment, application);

        // then
        verify(propertySources, never()).addFirst(any(PropertySource.class));
    }
}
