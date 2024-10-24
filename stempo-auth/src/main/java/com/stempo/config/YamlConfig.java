package com.stempo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

@Configuration
public class YamlConfig {

    @Bean
    public Yaml yamlParser(DumperOptions dumperOptions) {
        return new Yaml(dumperOptions);
    }

    @Bean
    public DumperOptions dumperOptions() {
        DumperOptions options = new DumperOptions();
        options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        options.setIndent(2);
        options.setPrettyFlow(true);
        return options;
    }
}
