package com.stempo.service;

import com.stempo.config.AesConfig;
import com.stempo.dto.DecryptedRecord;
import com.stempo.dto.response.RecordItemDto;
import com.stempo.mapper.RecordDtoMapper;
import com.stempo.model.Record;
import com.stempo.util.EncryptionUtils;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class RecordDecryptionService {

    private final EncryptionUtils encryptionUtils;
    private final AesConfig aesConfig;
    private final RecordDtoMapper mapper;

    public DecryptedRecord decryptedRecord(Record record) {
        String decryptedDeviceTag =
            encryptionUtils.decryptWithHashedIv(record.getDeviceTag(), aesConfig.getDeviceTagSecretKey());
        Double decryptedAccuracy = parseDouble(encryptionUtils.decrypt(record.getAccuracy()));
        Integer decryptedDuration = parseInteger(encryptionUtils.decrypt(record.getDuration()));
        Integer decryptedSteps = parseInteger(encryptionUtils.decrypt(record.getSteps()));
        Double decryptedLeftFootAverageSpeed = parseDouble(encryptionUtils.decrypt(record.getLeftFootAverageSpeed()));
        Double decryptedRightFootAverageSpeed = parseDouble(encryptionUtils.decrypt(record.getRightFootAverageSpeed()));
        Integer decryptedBit = parseInteger(encryptionUtils.decrypt(record.getBit()));
        Integer decryptedBpm = parseInteger(encryptionUtils.decrypt(record.getBpm()));

        return DecryptedRecord.builder()
            .id(record.getId())
            .deviceTag(decryptedDeviceTag)
            .accuracy(decryptedAccuracy)
            .duration(decryptedDuration)
            .steps(decryptedSteps)
            .leftFootAverageSpeed(decryptedLeftFootAverageSpeed)
            .rightFootAverageSpeed(decryptedRightFootAverageSpeed)
            .bit(decryptedBit)
            .bpm(decryptedBpm)
            .createdAt(record.getCreatedAt())
            .build();
    }

    public RecordItemDto decryptToRecordItemDto(Record record) {
        Double decryptedAccuracy = Double.parseDouble(encryptionUtils.decrypt(record.getAccuracy()));
        Integer decryptedDuration = Integer.parseInt(encryptionUtils.decrypt(record.getDuration()));
        Integer decryptedSteps = Integer.parseInt(encryptionUtils.decrypt(record.getSteps()));
        LocalDate recordDate = record.getCreatedAt().toLocalDate();

        return mapper.toDto(decryptedAccuracy, decryptedDuration, decryptedSteps, recordDate);
    }

    private Double parseDouble(String value) {
        try {
            if (value == null || value.isEmpty()) {
                return 0.0;
            }
            return Double.parseDouble(value);
        } catch (NumberFormatException e) {
            log.error("Error parsing double value: {}", value, e);
            return 0.0;
        }
    }

    private Integer parseInteger(String value) {
        try {
            if (value == null || value.isEmpty()) {
                return 0;
            }
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            log.error("Error parsing integer value: {}", value, e);
            return 0;
        }
    }
}
