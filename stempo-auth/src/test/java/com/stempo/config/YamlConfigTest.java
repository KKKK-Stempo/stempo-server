package com.stempo.config;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

@SpringBootTest(classes = YamlConfig.class)
class YamlConfigTest {

    @Autowired
    private Yaml yamlParser;

    @Autowired
    private DumperOptions dumperOptions;

    @Test
    void yamlParser_빈이_정상적으로_생성된다() {
        // then
        assertThat(yamlParser).isNotNull();
    }

    @Test
    void dumperOptions_빈이_정상적으로_생성된다() {
        // then
        assertThat(dumperOptions).isNotNull();
    }

    @Test
    void dumperOptions_설정이_정상적으로_적용된다() {
        // then
        assertThat(dumperOptions.getDefaultFlowStyle()).isEqualTo(DumperOptions.FlowStyle.BLOCK);
        assertThat(dumperOptions.getIndent()).isEqualTo(2);
        assertThat(dumperOptions.isPrettyFlow()).isTrue();
    }
}
