package com.stempo.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import com.stempo.config.AesConfig;
import com.stempo.dto.DecryptedHomework;
import com.stempo.model.Homework;
import com.stempo.util.EncryptionUtils;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class HomeworkDecryptionServiceTest {

    @Mock
    private AesConfig aesConfig;

    @Mock
    private EncryptionUtils encryptionUtils;

    @InjectMocks
    private HomeworkDecryptionService homeworkDecryptionService;

    @Test
    void 과제_데이터를_복호화_할_수_있다() {
        // given
        Long homeworkId = 1L;
        String encryptedDeviceTag = "encryptedTag";
        String encryptedDescription = "encryptedDesc";
        String expectedDecryptedDeviceTag = "decryptedTag";
        String expectedDecryptedDescription = "decryptedDesc";
        boolean completed = false;
        LocalDateTime createdAt = LocalDateTime.of(2025, 1, 1, 0, 0);
        LocalDateTime updatedAt = LocalDateTime.of(2025, 1, 1, 1, 0);

        Homework homework = Homework.builder()
            .id(homeworkId)
            .deviceTag(encryptedDeviceTag)
            .description(encryptedDescription)
            .completed(completed)
            .createdAt(createdAt)
            .updatedAt(updatedAt)
            .build();

        when(aesConfig.getDeviceTagSecretKey()).thenReturn("secretKey");
        when(encryptionUtils.decryptWithHashedIv(encryptedDeviceTag, "secretKey"))
            .thenReturn(expectedDecryptedDeviceTag);
        when(encryptionUtils.decrypt(encryptedDescription))
            .thenReturn(expectedDecryptedDescription);

        // when
        DecryptedHomework result = homeworkDecryptionService.decryptHomework(homework);

        // then
        assertThat(result.getId()).isEqualTo(homeworkId);
        assertThat(result.getDeviceTag()).isEqualTo(expectedDecryptedDeviceTag);
        assertThat(result.getDescription()).isEqualTo(expectedDecryptedDescription);
        assertThat(result.isCompleted()).isEqualTo(completed);
        assertThat(result.getCreatedAt()).isEqualTo(createdAt);
        assertThat(result.getUpdatedAt()).isEqualTo(updatedAt);
    }
}
