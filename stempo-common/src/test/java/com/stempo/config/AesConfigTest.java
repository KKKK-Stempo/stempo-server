package com.stempo.config;

import static org.assertj.core.api.Assertions.assertThat;

import com.stempo.util.EncryptionUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(classes = AesConfig.class)
@ActiveProfiles("test")
class AesConfigTest {

    @Autowired
    private AesConfig aesConfig;

    @Autowired
    private EncryptionUtils encryptionUtils;

    @Test
    void AesConfig가_정상적으로_로딩되는지_확인한다() {
        // then
        assertThat(aesConfig.getSecretKey()).isNotNull();
        assertThat(aesConfig.getIvLengthBytes()).isGreaterThan(0);
        assertThat(aesConfig.getGcmTagLengthBits()).isGreaterThan(0);
        assertThat(aesConfig.getDeviceTagSecretKey()).isNotNull();
    }

    @Test
    void EncryptionUtils_빈이_정상적으로_생성되는지_확인한다() {
        // then
        assertThat(encryptionUtils).isNotNull();
    }
}
