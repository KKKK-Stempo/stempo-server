package com.stempo.test.config;

import com.stempo.config.WhitelistProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

@EnableConfigurationProperties
public class TestConfiguration {

    @Bean
    public WhitelistProperties whitelistProperties() {
        return new WhitelistProperties();
    }
}
