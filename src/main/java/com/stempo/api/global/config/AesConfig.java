package com.stempo.api.global.config;

import com.stempo.api.global.util.EncryptionUtil;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Getter
public class AesConfig {

    @Value("${security.aes-key}")
    private String secretKey;

    @Value("${security.iv-length-bytes}")
    private int ivLengthBytes;

    @Value("${security.gcm-tag-length-bits}")
    private int gcmTagLengthBits;

    @Bean
    public EncryptionUtil encryptionUtil(AesConfig aesConfig) {
        return EncryptionUtil.create(aesConfig);
    }
}
