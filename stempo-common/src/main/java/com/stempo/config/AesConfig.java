package com.stempo.config;

import com.stempo.util.EncryptionUtils;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Getter
public class AesConfig {

    @Value("${security.aes.key}")
    private String secretKey;

    @Value("${security.aes.iv-length-bytes}")
    private int ivLengthBytes;

    @Value("${security.aes.gcm-tag-length-bits}")
    private int gcmTagLengthBits;

    @Value("${security.aes.device-tag-secret-key}")
    private String deviceTagSecretKey;

    @Bean
    public EncryptionUtils encryptionUtil(AesConfig aesConfig) {
        return EncryptionUtils.create(aesConfig);
    }
}
