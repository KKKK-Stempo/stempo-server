package com.stempo.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import com.stempo.dto.DecryptedHomework;
import com.stempo.dto.DecryptedRecord;
import com.stempo.dto.response.PersonalTrainingSettingsResponseDto;
import com.stempo.dto.response.UserDataResponseDto;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UserDataAggregationServiceImplTest {

    @Mock
    private RecordService recordService;

    @Mock
    private HomeworkService homeworkService;

    @InjectMocks
    private UserDataAggregationServiceImpl userDataAggregationService;

    @Test
    void 사용자_전체_이용_데이터를_성공적으로_반환한다() {
        // given
        List<String> deviceTags = Arrays.asList("device1", "device2");

        // device1에 대한 DecryptedRecord (생성일 기준 정렬 검증을 위해 순서를 섞어서 생성)
        DecryptedRecord record1 = DecryptedRecord.builder()
            .id(1L)
            .deviceTag("device1")
            .accuracy(90.0)
            .duration(100)
            .steps(50)
            .leftFootAverageSpeed(1.2)
            .rightFootAverageSpeed(1.1)
            .bit(4)
            .bpm(60)
            .createdAt(LocalDateTime.of(2025, 1, 1, 0, 0))
            .build();
        DecryptedRecord record2 = DecryptedRecord.builder()
            .id(2L)
            .deviceTag("device1")
            .accuracy(95.0)
            .duration(120)
            .steps(60)
            .leftFootAverageSpeed(1.3)
            .rightFootAverageSpeed(1.2)
            .bit(4)
            .bpm(65)
            .createdAt(LocalDateTime.of(2025, 1, 2, 0, 0))
            .build();
        // device2에 대한 DecryptedRecord
        DecryptedRecord record3 = DecryptedRecord.builder()
            .id(3L)
            .deviceTag("device2")
            .accuracy(88.0)
            .duration(80)
            .steps(40)
            .leftFootAverageSpeed(1.0)
            .rightFootAverageSpeed(0.9)
            .bit(4)
            .bpm(55)
            .createdAt(LocalDateTime.of(2025, 1, 1, 12, 0))
            .build();
        List<DecryptedRecord> records = Arrays.asList(record2, record1, record3);

        // device1, device2에 대한 DecryptedHomework 데이터
        DecryptedHomework homework1 = DecryptedHomework.builder()
            .id(1L)
            .deviceTag("device1")
            .description("스트레칭")
            .completed(false)
            .createdAt(LocalDateTime.of(2025, 1, 1, 1, 0))
            .updatedAt(LocalDateTime.of(2025, 1, 1, 1, 0))
            .build();
        DecryptedHomework homework2 = DecryptedHomework.builder()
            .id(2L)
            .deviceTag("device2")
            .description("걷기 연습")
            .completed(true)
            .createdAt(LocalDateTime.of(2025, 1, 1, 2, 0))
            .updatedAt(LocalDateTime.of(2025, 1, 1, 2, 0))
            .build();
        List<DecryptedHomework> homeworks = Arrays.asList(homework1, homework2);

        when(recordService.getByDeviceTags(deviceTags)).thenReturn(records);
        when(homeworkService.getByDeviceTags(deviceTags)).thenReturn(homeworks);

        // when
        List<UserDataResponseDto> result = userDataAggregationService.getUserData(deviceTags);

        // then
        // device1과 device2 두 건이 반환되어야 한다.
        assertEquals(2, result.size());

        // device1 데이터 검증
        UserDataResponseDto device1Data = result.stream()
            .filter(dto -> "device1".equals(dto.getDeviceTag()))
            .findFirst()
            .orElse(null);
        assertNotNull(device1Data);
        assertEquals(2, device1Data.getRecords().size());
        // record1의 createdAt이 record2의 createdAt보다 빠른지 확인 (정렬)
        assertTrue(
            device1Data.getRecords().get(0).getCreatedAt().isBefore(device1Data.getRecords().get(1).getCreatedAt()));

        // device2 데이터 검증
        UserDataResponseDto device2Data = result.stream()
            .filter(dto -> "device2".equals(dto.getDeviceTag()))
            .findFirst()
            .orElse(null);
        assertNotNull(device2Data);
        assertEquals(1, device2Data.getRecords().size());
    }

    @Test
    void 사용자_보행_분석_지표_설정값을_성공적으로_반환한다() {
        // given
        List<String> deviceTags = List.of("device1");

        // device1에 대한 DecryptedRecord (정렬 검증을 위해 순서를 섞어서 생성)
        DecryptedRecord record1 = DecryptedRecord.builder()
            .id(1L)
            .deviceTag("device1")
            .accuracy(85.0)
            .duration(90)
            .steps(45)
            .leftFootAverageSpeed(1.1)
            .rightFootAverageSpeed(1.0)
            .bit(4)
            .bpm(58)
            .createdAt(LocalDateTime.of(2025, 1, 1, 0, 0))
            .build();
        DecryptedRecord record2 = DecryptedRecord.builder()
            .id(2L)
            .deviceTag("device1")
            .accuracy(92.0)
            .duration(110)
            .steps(55)
            .leftFootAverageSpeed(1.2)
            .rightFootAverageSpeed(1.1)
            .bit(4)
            .bpm(62)
            .createdAt(LocalDateTime.of(2025, 1, 2, 0, 0))
            .build();
        List<DecryptedRecord> records = Arrays.asList(record2, record1);

        // device1에 대한 DecryptedHomework 데이터
        DecryptedHomework homework1 = DecryptedHomework.builder()
            .id(1L)
            .deviceTag("device1")
            .description("매일 스트레칭")
            .completed(false)
            .createdAt(LocalDateTime.of(2025, 1, 1, 1, 0))
            .updatedAt(LocalDateTime.of(2025, 1, 1, 1, 0))
            .build();
        List<DecryptedHomework> homeworks = Collections.singletonList(homework1);

        when(recordService.getByDeviceTags(deviceTags)).thenReturn(records);
        when(homeworkService.getByDeviceTags(deviceTags)).thenReturn(homeworks);

        // when
        List<PersonalTrainingSettingsResponseDto> result = userDataAggregationService.getPersonalTrainingSettings(
            deviceTags);

        // then
        // 반환된 데이터에서 deviceTag가 일치하며, 초기 분석 지표로 사용된 record가 createdAt이 가장 빠른 데이터인지 확인한다.
        assertEquals(1, result.size());
        PersonalTrainingSettingsResponseDto settings = result.getFirst();
        assertEquals("device1", settings.getDeviceTag());

        // 초기 보행 훈련 데이터는 createdAt이 가장 빠른 record여야 함 (record1)
        assertNotNull(settings.getInitialTraining());
        assertEquals(record1.getAccuracy(), settings.getInitialTraining().getAccuracy());
        assertEquals(record1.getCreatedAt(), settings.getInitialTraining().getCreatedAt());

        // 과제 데이터 검증
        assertEquals(1, settings.getHomeworks().size());
        assertEquals("매일 스트레칭", settings.getHomeworks().getFirst().getDescription());
    }
}
