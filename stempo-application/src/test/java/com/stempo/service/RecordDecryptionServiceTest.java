package com.stempo.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import com.stempo.config.AesConfig;
import com.stempo.dto.DecryptedRecord;
import com.stempo.dto.response.RecordItemDto;
import com.stempo.mapper.RecordDtoMapper;
import com.stempo.model.Record;
import com.stempo.util.EncryptionUtils;
import java.time.LocalDate;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class RecordDecryptionServiceTest {

    @Mock
    private EncryptionUtils encryptionUtils;

    @Mock
    private AesConfig aesConfig;

    @Mock
    private RecordDtoMapper mapper;

    @InjectMocks
    private RecordDecryptionService recordDecryptionService;

    @Test
    void 보행_훈련_기록을_복호화할_수_있다() {
        // given
        Record encryptedRecord = Record.builder()
            .id(1L)
            .deviceTag("encryptedDeviceTag")
            .accuracy("encryptedAccuracy")
            .duration("encryptedDuration")
            .steps("encryptedSteps")
            .leftFootAverageSpeed("encryptedLeftSpeed")
            .rightFootAverageSpeed("encryptedRightSpeed")
            .bit("encryptedBit")
            .bpm("encryptedBpm")
            .createdAt(LocalDateTime.of(2025, 1, 1, 12, 0))
            .build();

        String secretKey = "secretKey";
        when(aesConfig.getDeviceTagSecretKey()).thenReturn(secretKey);
        when(encryptionUtils.decryptWithHashedIv("encryptedDeviceTag", secretKey))
            .thenReturn("decryptedDeviceTag");
        when(encryptionUtils.decrypt("encryptedAccuracy")).thenReturn("0.5");
        when(encryptionUtils.decrypt("encryptedDuration")).thenReturn("30");
        when(encryptionUtils.decrypt("encryptedSteps")).thenReturn("100");
        when(encryptionUtils.decrypt("encryptedLeftSpeed")).thenReturn("0.7");
        when(encryptionUtils.decrypt("encryptedRightSpeed")).thenReturn("0.8");
        when(encryptionUtils.decrypt("encryptedBit")).thenReturn("4");
        when(encryptionUtils.decrypt("encryptedBpm")).thenReturn("60");

        // when
        DecryptedRecord decryptedRecord = recordDecryptionService.decryptedRecord(encryptedRecord);

        // then
        assertThat(decryptedRecord.getId()).isEqualTo(1L);
        assertThat(decryptedRecord.getDeviceTag()).isEqualTo("decryptedDeviceTag");
        assertThat(decryptedRecord.getAccuracy()).isEqualTo(0.5);
        assertThat(decryptedRecord.getDuration()).isEqualTo(30);
        assertThat(decryptedRecord.getSteps()).isEqualTo(100);
        assertThat(decryptedRecord.getLeftFootAverageSpeed()).isEqualTo(0.7);
        assertThat(decryptedRecord.getRightFootAverageSpeed()).isEqualTo(0.8);
        assertThat(decryptedRecord.getBit()).isEqualTo(4);
        assertThat(decryptedRecord.getBpm()).isEqualTo(60);
        assertThat(decryptedRecord.getCreatedAt()).isEqualTo(LocalDateTime.of(2025, 1, 1, 12, 0));
    }

    @Test
    void 보행_훈련_기록을_RecordItemDTO로_변환한다() {
        // given
        LocalDateTime createdAt = LocalDateTime.of(2025, 2, 1, 14, 30);
        Record encryptedRecord = Record.builder()
            .id(2L)
            .deviceTag("dummyTag")
            .accuracy("encryptedAccuracy")
            .duration("encryptedDuration")
            .steps("encryptedSteps")
            .leftFootAverageSpeed("irrelevant")
            .rightFootAverageSpeed("irrelevant")
            .bit("irrelevant")
            .bpm("irrelevant")
            .createdAt(createdAt)
            .build();

        // 복호화된 숫자 문자열값 설정
        when(encryptionUtils.decrypt("encryptedAccuracy")).thenReturn("0.75");
        when(encryptionUtils.decrypt("encryptedDuration")).thenReturn("45");
        when(encryptionUtils.decrypt("encryptedSteps")).thenReturn("120");

        LocalDate expectedDate = createdAt.toLocalDate();

        // mapper.toDto()가 반환할 RecordItemDto 더미 객체 생성
        RecordItemDto expectedDto = RecordItemDto.builder()
            .accuracy(0.75)
            .duration(45)
            .steps(120)
            .date(expectedDate)
            .build();

        when(mapper.toDto(0.75, 45, 120, expectedDate)).thenReturn(expectedDto);

        // when
        RecordItemDto result = recordDecryptionService.decryptToRecordItemDto(encryptedRecord);

        // then
        assertThat(result).isEqualTo(expectedDto);
    }
}
