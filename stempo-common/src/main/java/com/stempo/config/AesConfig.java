package com.stempo.config;

import com.stempo.util.EncryptionUtils;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Getter
public class AesConfig implements EncryptionConfig {

    private final String secretKey;
    private final int ivLengthBytes;
    private final int gcmTagLengthBits;
    private final String deviceTagSecretKey;

    public AesConfig(
        @Value("${security.aes.key}") String secretKey,
        @Value("${security.aes.iv-length-bytes}") int ivLengthBytes,
        @Value("${security.aes.gcm-tag-length-bits}") int gcmTagLengthBits,
        @Value("${security.aes.device-tag-secret-key}") String deviceTagSecretKey
    ) {
        this.secretKey = secretKey;
        this.ivLengthBytes = ivLengthBytes;
        this.gcmTagLengthBits = gcmTagLengthBits;
        this.deviceTagSecretKey = deviceTagSecretKey;
    }

    @Bean
    public EncryptionUtils encryptionUtils() {
        return new EncryptionUtils(this);
    }
}
