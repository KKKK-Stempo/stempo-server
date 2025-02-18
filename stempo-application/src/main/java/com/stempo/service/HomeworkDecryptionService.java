package com.stempo.service;

import com.stempo.config.AesConfig;
import com.stempo.dto.DecryptedHomework;
import com.stempo.model.Homework;
import com.stempo.util.EncryptionUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class HomeworkDecryptionService {

    private final AesConfig aesConfig;
    private final EncryptionUtils encryptionUtils;

    public DecryptedHomework decryptHomework(Homework homework) {
        String decryptedDeviceTag =
            encryptionUtils.decryptWithHashedIv(homework.getDeviceTag(), aesConfig.getDeviceTagSecretKey());
        String decryptedDescription = encryptionUtils.decrypt(homework.getDescription());
        return DecryptedHomework.builder()
            .id(homework.getId())
            .deviceTag(decryptedDeviceTag)
            .description(decryptedDescription)
            .completed(homework.getCompleted())
            .createdAt(homework.getCreatedAt())
            .updatedAt(homework.getUpdatedAt())
            .build();
    }
}
