package com.stempo.api.global.config;

import com.stempo.api.global.util.EncryptionUtil;
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
    public EncryptionUtil encryptionUtil(AesConfig aesConfig) {
        return EncryptionUtil.create(aesConfig);
    }
}
