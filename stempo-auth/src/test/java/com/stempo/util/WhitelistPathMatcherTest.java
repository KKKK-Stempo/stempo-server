package com.stempo.util;

import static org.assertj.core.api.Assertions.assertThat;

import com.stempo.config.WhitelistProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class WhitelistPathMatcherTest {

    @BeforeEach
    void setUp() {
        WhitelistProperties whitelistProperties = Mockito.mock(WhitelistProperties.class);
        WhitelistProperties.Patterns patterns = new WhitelistProperties.Patterns();
        patterns.setActuator(new String[]{"/actuator/.*"});
        patterns.setApiDocs(new String[]{"/v3/api-docs/.*", "/swagger-ui/.*", "/swagger-ui/index.html"});

        Mockito.when(whitelistProperties.getPatterns()).thenReturn(patterns);

        WhitelistPathMatcher whitelistPathMatcher = new WhitelistPathMatcher(whitelistProperties);
        whitelistPathMatcher.afterPropertiesSet();
    }

    @Test
    void ApiDocs_경로인지_확인한다() {
        // when
        boolean result1 = WhitelistPathMatcher.isApiDocsRequest("/v3/api-docs/example");
        boolean result2 = WhitelistPathMatcher.isApiDocsRequest("/swagger-ui/index.html");
        boolean result3 = WhitelistPathMatcher.isApiDocsRequest("/non-api-docs-path");

        // then
        assertThat(result1).isTrue();
        assertThat(result2).isTrue();
        assertThat(result3).isFalse();
    }

    @Test
    void Actuator_경로인지_확인한다() {
        // when
        boolean result1 = WhitelistPathMatcher.isActuatorRequest("/actuator/health");
        boolean result2 = WhitelistPathMatcher.isActuatorRequest("/actuator/info");
        boolean result3 = WhitelistPathMatcher.isActuatorRequest("/non-actuator-path");

        // then
        assertThat(result1).isTrue();
        assertThat(result2).isTrue();
        assertThat(result3).isFalse();
    }

    @Test
    void Whitelist_경로인지_확인한다() {
        // when
        boolean result1 = WhitelistPathMatcher.isWhitelistRequest("/actuator/health");
        boolean result2 = WhitelistPathMatcher.isWhitelistRequest("/non-whitelist-path");

        // then
        assertThat(result1).isTrue();
        assertThat(result2).isFalse();
    }

    @Test
    void ApiDocs_Index_엔드포인트인지_확인한다() {
        // when
        boolean result1 = WhitelistPathMatcher.isApiDocsIndexEndpoint("/swagger-ui/index.html");
        boolean result2 = WhitelistPathMatcher.isApiDocsIndexEndpoint("/v3/api-docs");

        // then
        assertThat(result1).isTrue();
        assertThat(result2).isFalse();
    }

    @Test
    void 패턴과_일치하는지_확인한다() {
        // given
        String[] patterns = {"/test/.*", "/example/.*"};

        // when
        boolean result1 = WhitelistPathMatcher.isPatternMatch("/test/path", patterns);
        boolean result2 = WhitelistPathMatcher.isPatternMatch("/example/path", patterns);
        boolean result3 = WhitelistPathMatcher.isPatternMatch("/non-matching-path", patterns);

        // then
        assertThat(result1).isTrue();
        assertThat(result2).isTrue();
        assertThat(result3).isFalse();
    }
}
