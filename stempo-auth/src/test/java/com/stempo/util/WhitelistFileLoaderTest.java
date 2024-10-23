package com.stempo.util;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.yaml.snakeyaml.Yaml;

class WhitelistFileLoaderTest {

    @TempDir
    Path tempDir;

    private WhitelistFileLoader whitelistFileLoader;

    @BeforeEach
    void setUp() {
        String whitelistPath = tempDir.resolve("whitelist.yml").toString();
        whitelistFileLoader = new WhitelistFileLoader(new Yaml(), whitelistPath);
    }

    @Test
    void 화이트리스트_파일이_존재하지_않으면_기본_파일을_생성한다() {
        // when
        List<String> result = whitelistFileLoader.loadWhitelistIps();

        // then
        assertThat(result).contains("*");
        Path whitelistPath = tempDir.resolve("whitelist.yml");
        assertThat(Files.exists(whitelistPath)).isTrue();
    }

    @Test
    void 화이트리스트_파일에서_정상적으로_IP를_로드한다() throws IOException {
        // given
        Path whitelistPath = tempDir.resolve("whitelist.yml");
        Files.writeString(whitelistPath, "whitelist:\n  fixedIps: [\"192.168.1.1\"]\n  temporaryIps: [\"*\"]");

        // when
        List<String> result = whitelistFileLoader.loadWhitelistIps();

        // then
        assertThat(result).containsExactly("192.168.1.1", "*");
    }

    @Test
    void 화이트리스트_파일이_비어있으면_빈_목록을_반환한다() throws IOException {
        // given
        Path whitelistPath = tempDir.resolve("whitelist.yml");
        Files.writeString(whitelistPath, "");

        // when
        List<String> result = whitelistFileLoader.loadWhitelistIps();

        // then
        assertThat(result).isEmpty();
    }

    @Test
    void YAML_파싱_실패시_빈_목록을_반환한다() throws IOException {
        // given
        Path whitelistPath = tempDir.resolve("whitelist.yml");
        Files.writeString(whitelistPath, "invalid_yaml_content");

        // when
        List<String> result = whitelistFileLoader.loadWhitelistIps();

        // then
        assertThat(result).isEmpty();
    }
}
