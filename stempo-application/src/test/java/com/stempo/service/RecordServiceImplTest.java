package com.stempo.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.stempo.dto.DecryptedRecord;
import com.stempo.dto.request.RecordRequestDto;
import com.stempo.dto.response.RecordItemDto;
import com.stempo.dto.response.RecordResponseDto;
import com.stempo.dto.response.RecordStatisticsResponseDto;
import com.stempo.mapper.RecordDtoMapper;
import com.stempo.model.Record;
import com.stempo.repository.RecordRepository;
import com.stempo.util.EncryptionUtils;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class RecordServiceImplTest {

    @Mock
    private UserService userService;

    @Mock
    private RecordRepository recordRepository;

    @Mock
    private RecordDecryptionService recordDecryptionService;

    @Mock
    private RecordDtoMapper mapper;

    @Mock
    private EncryptionUtils encryptionUtils;

    @InjectMocks
    private RecordServiceImpl recordService;

    private RecordRequestDto recordRequestDto;
    private Record trainingRecord;
    private String deviceTag;

    @BeforeEach
    void setUp() {
        deviceTag = "test-device-tag";
        recordRequestDto = new RecordRequestDto();
        recordRequestDto.setAccuracy(95.5);
        recordRequestDto.setDuration(120);
        recordRequestDto.setSteps(1000);
        recordRequestDto.setLeftFootAverageSpeed(1.0);
        recordRequestDto.setRightFootAverageSpeed(1.0);
        recordRequestDto.setBit(4);
        recordRequestDto.setBpm(120);

        trainingRecord = Record.builder()
            .id(1L)
            .deviceTag(deviceTag)
            .accuracy("encrypted-accuracy")
            .duration("encrypted-duration")
            .steps("encrypted-steps")
            .leftFootAverageSpeed("encrypted-left-foot-average-speed")
            .rightFootAverageSpeed("encrypted-right-foot-average-speed")
            .bit("encrypted-bit")
            .bpm("encrypted-bpm")
            .createdAt(LocalDateTime.now())
            .build();
    }

    @Test
    void 레코드를_저장한다() {
        // given
        when(encryptionUtils.encrypt(anyString())).thenReturn("encrypted-value");
        when(recordRepository.save(any(Record.class))).thenReturn(trainingRecord);

        // when
        String result = recordService.recordTrainingData(deviceTag, recordRequestDto);

        // then
        assertThat(result).isEqualTo(deviceTag);
        verify(encryptionUtils, times(7)).encrypt(anyString());
        verify(recordRepository).save(any(Record.class));
    }

    @Test
    void 레코드를_날짜_범위로_조회한다() {
        // given
        LocalDate startDate = LocalDate.of(2024, 10, 21);
        LocalDate endDate = LocalDate.of(2024, 10, 27);
        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.plusDays(1).atStartOfDay();

        String deviceTag = "device123";

        // 테스트용 Record 객체 생성
        Record trainingRecord = Record.builder()
            .id(1L)
            .deviceTag(deviceTag)
            .accuracy("encrypted-accuracy")
            .duration("encrypted-duration")
            .steps("encrypted-steps")
            .createdAt(LocalDateTime.of(2024, 10, 22, 12, 0))
            .build();
        Record latestRecord = Record.builder()
            .id(2L)
            .deviceTag(deviceTag)
            .accuracy("encrypted-accuracy")
            .duration("encrypted-duration")
            .steps("encrypted-steps")
            .createdAt(startDateTime.minusDays(1))
            .build();

        when(recordRepository.findLatestBeforeStartDate(deviceTag, startDateTime))
            .thenReturn(Optional.of(latestRecord));
        when(recordRepository.findByDateBetween(deviceTag, startDateTime, endDateTime))
            .thenReturn(List.of(trainingRecord));

        RecordItemDto latestRecordDto = RecordItemDto.builder()
            .accuracy(95.5)
            .duration(120)
            .steps(1000)
            .build();
        RecordItemDto trainingRecordDto = RecordItemDto.builder()
            .accuracy(95.5)
            .duration(120)
            .steps(1000)
            .build();
        when(recordDecryptionService.decryptToRecordItemDto(latestRecord))
            .thenReturn(latestRecordDto);
        when(recordDecryptionService.decryptToRecordItemDto(trainingRecord))
            .thenReturn(trainingRecordDto);

        List<RecordItemDto> combinedRecords = List.of(latestRecordDto, trainingRecordDto);
        RecordResponseDto expectedResponse = RecordResponseDto.builder()
            .accuracyAverage(96)
            .records(combinedRecords)
            .build();
        when(mapper.toDto(anyInt(), any(List.class))).thenReturn(expectedResponse);

        // when
        RecordResponseDto result = recordService.getRecordsByDateRange(deviceTag, startDate, endDate);

        // then
        assertThat(result.getAccuracyAverage()).isEqualTo(96);
        assertThat(result.getRecords()).hasSize(2);
        verify(recordRepository).findLatestBeforeStartDate(deviceTag, startDateTime);
        verify(recordRepository).findByDateBetween(deviceTag, startDateTime, endDateTime);
        verify(recordDecryptionService).decryptToRecordItemDto(latestRecord);
        verify(recordDecryptionService).decryptToRecordItemDto(trainingRecord);
        verify(mapper).toDto(anyInt(), any(List.class));
    }


    @Test
    void startDate_이전의_최신_데이터가_없는_경우_startDate_이전_날짜로_0으로_초기화된_값을_생성한다() {
        // given
        LocalDate startDate = LocalDate.of(2024, 10, 21);
        LocalDate endDate = LocalDate.of(2024, 10, 27);
        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.plusDays(1).atStartOfDay();

        when(recordRepository.findLatestBeforeStartDate(deviceTag, startDateTime))
            .thenReturn(Optional.empty());
        when(recordRepository.findByDateBetween(deviceTag, startDateTime, endDateTime))
            .thenReturn(List.of());
        when(mapper.toDto(anyDouble(), anyInt(), anyInt(), eq(startDate.minusDays(1))))
            .thenReturn(RecordItemDto.builder()
                .accuracy(0.0)
                .duration(0)
                .steps(0)
                .build());
        when(mapper.toDto(anyInt(), any(List.class)))
            .thenReturn(RecordResponseDto.builder()
                .accuracyAverage(0)
                .records(List.of(
                    RecordItemDto.builder()
                        .accuracy(0.0)
                        .duration(0)
                        .steps(0)
                        .build()
                ))
                .build());

        // when
        RecordResponseDto result = recordService.getRecordsByDateRange(deviceTag, startDate, endDate);

        // then
        assertThat(result.getAccuracyAverage()).isZero();
        assertThat(result.getRecords()).hasSize(1);
        assertThat(result.getRecords().getFirst().getAccuracy()).isEqualTo(0.0);
        assertThat(result.getRecords().getFirst().getDuration()).isZero();
        assertThat(result.getRecords().getFirst().getSteps()).isZero();
        verify(recordRepository).findLatestBeforeStartDate(deviceTag, startDateTime);
        verify(recordRepository).findByDateBetween(deviceTag, startDateTime, endDateTime);
        verify(mapper).toDto(anyDouble(), anyInt(), anyInt(), eq(startDate.minusDays(1)));
        verify(mapper).toDto(anyInt(), any(List.class));
    }

    @Test
    void 통계정보를_조회한다() {
        // given
        when(recordRepository.countByDeviceTagAndCreatedAtBetween(anyString(), any(LocalDateTime.class),
            any(LocalDateTime.class)))
            .thenReturn(2, 5); // todayWalkTrainingCount, weeklyWalkTrainingCount
        when(recordRepository.findCreatedAtByDeviceTagOrderByCreatedAtDesc(deviceTag))
            .thenReturn(createMockedCreatedAtList());
        when(mapper.toDto(anyInt(), anyInt(), anyInt()))
            .thenReturn(RecordStatisticsResponseDto.builder()
                .todayWalkTrainingCount(2)
                .weeklyWalkTrainingCount(5)
                .consecutiveWalkTrainingDays(3)
                .build()
            );

        // when
        RecordStatisticsResponseDto result = recordService.getRecordStatistics(deviceTag);

        // then
        assertThat(result.getTodayWalkTrainingCount()).isEqualTo(2);
        assertThat(result.getWeeklyWalkTrainingCount()).isEqualTo(5);
        assertThat(result.getConsecutiveWalkTrainingDays()).isEqualTo(3);
        verify(recordRepository, times(2))
            .countByDeviceTagAndCreatedAtBetween(anyString(), any(LocalDateTime.class), any(LocalDateTime.class));
        verify(recordRepository).findCreatedAtByDeviceTagOrderByCreatedAtDesc(deviceTag);
        verify(mapper).toDto(2, 5, 3);
    }

    @Test
    void 디바이스태그로_복호화된_레코드_목록을_조회한다() {
        // given
        List<String> deviceTags = List.of("tag1", "tag2");
        when(userService.encryptDeviceTag("tag1")).thenReturn("encryptedTag1");
        when(userService.encryptDeviceTag("tag2")).thenReturn("encryptedTag2");

        Record record1 = Record.builder()
            .id(1L)
            .deviceTag("encryptedTag1")
            .accuracy("encryptedAccuracy1")
            .duration("encryptedDuration1")
            .steps("encryptedSteps1")
            .leftFootAverageSpeed("encryptedLeftSpeed1")
            .rightFootAverageSpeed("encryptedRightSpeed1")
            .bit("encryptedBit1")
            .bpm("encryptedBpm1")
            .createdAt(LocalDateTime.of(2025, 1, 1, 12, 0))
            .build();
        Record record2 = Record.builder()
            .id(2L)
            .deviceTag("encryptedTag2")
            .accuracy("encryptedAccuracy2")
            .duration("encryptedDuration2")
            .steps("encryptedSteps2")
            .leftFootAverageSpeed("encryptedLeftSpeed2")
            .rightFootAverageSpeed("encryptedRightSpeed2")
            .bit("encryptedBit2")
            .bpm("encryptedBpm2")
            .createdAt(LocalDateTime.of(2025, 1, 2, 12, 0))
            .build();

        List<Record> repositoryRecords = List.of(record1, record2);
        when(recordRepository.findRecordsByDeviceTags(List.of("encryptedTag1", "encryptedTag2")))
            .thenReturn(repositoryRecords);

        DecryptedRecord decryptedRecord1 = DecryptedRecord.builder()
            .id(1L)
            .deviceTag("decryptedTag1")
            .accuracy(95.0)
            .duration(120)
            .steps(1000)
            .leftFootAverageSpeed(1.0)
            .rightFootAverageSpeed(1.0)
            .bit(4)
            .bpm(120)
            .createdAt(record1.getCreatedAt())
            .build();
        DecryptedRecord decryptedRecord2 = DecryptedRecord.builder()
            .id(2L)
            .deviceTag("decryptedTag2")
            .accuracy(96.0)
            .duration(130)
            .steps(1100)
            .leftFootAverageSpeed(1.1)
            .rightFootAverageSpeed(1.1)
            .bit(5)
            .bpm(125)
            .createdAt(record2.getCreatedAt())
            .build();

        when(recordDecryptionService.decryptedRecord(record1)).thenReturn(decryptedRecord1);
        when(recordDecryptionService.decryptedRecord(record2)).thenReturn(decryptedRecord2);

        // when
        List<DecryptedRecord> result = recordService.getByDeviceTags(deviceTags);

        // then
        assertThat(result).hasSize(2)
            .containsExactlyInAnyOrder(decryptedRecord1, decryptedRecord2);
    }

    @Test
    void 날짜범위로_디바이스태그에_해당하는_복호화된_레코드_목록을_조회한다() {
        // given
        List<String> deviceTags = List.of("tag1", "tag2");
        LocalDate startDate = LocalDate.of(2025, 1, 1);
        LocalDate endDate = LocalDate.of(2025, 1, 31);

        when(userService.encryptDeviceTag("tag1")).thenReturn("encryptedTag1");
        when(userService.encryptDeviceTag("tag2")).thenReturn("encryptedTag2");

        Record record1 = Record.builder()
            .id(1L)
            .deviceTag("encryptedTag1")
            .accuracy("encryptedAccuracy1")
            .duration("encryptedDuration1")
            .steps("encryptedSteps1")
            .leftFootAverageSpeed("encryptedLeftSpeed1")
            .rightFootAverageSpeed("encryptedRightSpeed1")
            .bit("encryptedBit1")
            .bpm("encryptedBpm1")
            .createdAt(LocalDateTime.of(2025, 1, 15, 12, 0))
            .build();
        Record record2 = Record.builder()
            .id(2L)
            .deviceTag("encryptedTag2")
            .accuracy("encryptedAccuracy2")
            .duration("encryptedDuration2")
            .steps("encryptedSteps2")
            .leftFootAverageSpeed("encryptedLeftSpeed2")
            .rightFootAverageSpeed("encryptedRightSpeed2")
            .bit("encryptedBit2")
            .bpm("encryptedBpm2")
            .createdAt(LocalDateTime.of(2025, 1, 20, 12, 0))
            .build();

        List<Record> repositoryRecords = List.of(record1, record2);
        when(recordRepository.findRecordsByDeviceTagsAndDateRange(
            List.of("encryptedTag1", "encryptedTag2"), startDate, endDate))
            .thenReturn(repositoryRecords);

        DecryptedRecord decryptedRecord1 = DecryptedRecord.builder()
            .id(1L)
            .deviceTag("decryptedTag1")
            .accuracy(95.0)
            .duration(120)
            .steps(1000)
            .leftFootAverageSpeed(1.0)
            .rightFootAverageSpeed(1.0)
            .bit(4)
            .bpm(120)
            .createdAt(record1.getCreatedAt())
            .build();
        DecryptedRecord decryptedRecord2 = DecryptedRecord.builder()
            .id(2L)
            .deviceTag("decryptedTag2")
            .accuracy(96.0)
            .duration(130)
            .steps(1100)
            .leftFootAverageSpeed(1.1)
            .rightFootAverageSpeed(1.1)
            .bit(5)
            .bpm(125)
            .createdAt(record2.getCreatedAt())
            .build();

        when(recordDecryptionService.decryptedRecord(record1)).thenReturn(decryptedRecord1);
        when(recordDecryptionService.decryptedRecord(record2)).thenReturn(decryptedRecord2);

        // when
        List<DecryptedRecord> result = recordService.getByDeviceTagsAndDateRange(deviceTags, startDate, endDate);

        // then
        assertThat(result).hasSize(2).containsExactlyInAnyOrder(decryptedRecord1, decryptedRecord2);
    }

    private List<LocalDateTime> createMockedCreatedAtList() {
        LocalDateTime now = LocalDateTime.now();
        return List.of(
            now,
            now.minusDays(1),
            now.minusDays(2),
            now.minusDays(4)
        );
    }
}
