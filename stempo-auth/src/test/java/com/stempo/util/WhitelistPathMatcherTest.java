package com.stempo.util;

import com.stempo.config.WhitelistProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.assertj.core.api.Assertions.assertThat;

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
        assertThat(WhitelistPathMatcher.isApiDocsRequest("/v3/api-docs/example")).isTrue();
        assertThat(WhitelistPathMatcher.isApiDocsRequest("/swagger-ui/index.html")).isTrue();
        assertThat(WhitelistPathMatcher.isApiDocsRequest("/non-api-docs-path")).isFalse();
    }

    @Test
    void Actuator_경로인지_확인한다() {
        assertThat(WhitelistPathMatcher.isActuatorRequest("/actuator/health")).isTrue();
        assertThat(WhitelistPathMatcher.isActuatorRequest("/actuator/info")).isTrue();
        assertThat(WhitelistPathMatcher.isActuatorRequest("/non-actuator-path")).isFalse();
    }

    @Test
    void Whitelist_경로인지_확인한다() {
        assertThat(WhitelistPathMatcher.isWhitelistRequest("/actuator/health")).isTrue();
        assertThat(WhitelistPathMatcher.isWhitelistRequest("/non-whitelist-path")).isFalse();
    }

    @Test
    void ApiDocs_Index_엔드포인트인지_확인한다() {
        assertThat(WhitelistPathMatcher.isApiDocsIndexEndpoint("/swagger-ui/index.html")).isTrue();
        assertThat(WhitelistPathMatcher.isApiDocsIndexEndpoint("/v3/api-docs")).isFalse();
    }

    @Test
    void 패턴과_일치하는지_확인한다() {
        String[] patterns = {"/test/.*", "/example/.*"};
        assertThat(WhitelistPathMatcher.isPatternMatch("/test/path", patterns)).isTrue();
        assertThat(WhitelistPathMatcher.isPatternMatch("/example/path", patterns)).isTrue();
        assertThat(WhitelistPathMatcher.isPatternMatch("/non-matching-path", patterns)).isFalse();
    }
}
