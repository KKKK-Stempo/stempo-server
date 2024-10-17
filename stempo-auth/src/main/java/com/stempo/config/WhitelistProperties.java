package com.stempo.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.stream.Stream;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "security.whitelist")
public class WhitelistProperties {

    private boolean enabled;
    private String path;

    private Account account = new Account();
    private Patterns patterns = new Patterns();

    @Getter
    @Setter
    public static class Account {
        private String username;
        private String password;
        private String role;
    }

    @Getter
    @Setter
    public static class Patterns {
        private String[] actuator;
        private String[] apiDocs;

        public String[] getWhitelistPatterns() {
            return Stream.of(apiDocs, actuator)
                    .flatMap(Arrays::stream)
                    .toArray(String[]::new);
        }
    }
}
