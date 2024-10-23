package com.stempo.config;

import static org.assertj.core.api.Assertions.assertThat;

import com.stempo.test.config.TestConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;


@SpringBootTest(classes = TestConfiguration.class)
@ActiveProfiles("test")
class WhitelistPropertiesTest {

    @Autowired
    private WhitelistProperties whitelistProperties;

    @Test
    void whitelistProperties_빈이_정상적으로_생성된다() {
        // then
        assertThat(whitelistProperties).isNotNull();
    }

    @Test
    void whitelistProperties_설정값이_정상적으로_적용된다() {
        // then
        assertThat(whitelistProperties.isEnabled()).isTrue();
        assertThat(whitelistProperties.getPath()).isEqualTo("/whitelist");

        // Account 설정값 확인
        WhitelistProperties.Account account = whitelistProperties.getAccount();
        assertThat(account.getUsername()).isEqualTo("testuser");
        assertThat(account.getPassword()).isEqualTo("testpass");
        assertThat(account.getRole()).isEqualTo("ADMIN");

        // Patterns 설정값 확인
        WhitelistProperties.Patterns patterns = whitelistProperties.getPatterns();
        assertThat(patterns.getActuator()).containsExactly("/actuator/health", "/actuator/info");
        assertThat(patterns.getApiDocs()).containsExactly("/v3/api-docs", "/swagger-ui.html");
    }

    @Test
    void patterns_화이트리스트_패턴이_정상적으로_적용된다() {
        // when
        String[] whitelistPatterns = whitelistProperties.getPatterns().getWhitelistPatterns();

        // then
        assertThat(whitelistPatterns).containsExactlyInAnyOrder(
                "/actuator/health",
                "/actuator/info",
                "/v3/api-docs",
                "/swagger-ui.html"
        );
    }
}
