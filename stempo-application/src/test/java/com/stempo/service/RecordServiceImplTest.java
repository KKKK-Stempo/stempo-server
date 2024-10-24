package com.stempo.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.stempo.dto.request.RecordRequestDto;
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
    private RecordDtoMapper mapper;

    @Mock
    private EncryptionUtils encryptionUtils;

    @InjectMocks
    private RecordServiceImpl recordService;

    private RecordRequestDto recordRequestDto;
    private Record record;
    private String deviceTag;

    @BeforeEach
    void setUp() {
        deviceTag = "test-device-tag";
        recordRequestDto = new RecordRequestDto();
        recordRequestDto.setAccuracy(95.5);
        recordRequestDto.setDuration(120);
        recordRequestDto.setSteps(1000);

        record = Record.builder()
                .id(1L)
                .deviceTag(deviceTag)
                .accuracy("encrypted-accuracy")
                .duration("encrypted-duration")
                .steps("encrypted-steps")
                .createdAt(LocalDateTime.now())
                .build();
    }

    @Test
    void 레코드를_저장한다() {
        // given
        when(userService.getCurrentDeviceTag()).thenReturn(deviceTag);
        when(encryptionUtils.encrypt(anyString())).thenReturn("encrypted-value");
        when(recordRepository.save(any(Record.class))).thenReturn(record);

        // when
        String result = recordService.record(recordRequestDto);

        // then
        assertThat(result).isEqualTo(deviceTag);
        verify(userService).getCurrentDeviceTag();
        verify(encryptionUtils, times(3)).encrypt(anyString());
        verify(recordRepository).save(any(Record.class));
    }

    @Test
    void 레코드를_날짜_범위로_조회한다() {
        // given
        LocalDate startDate = LocalDate.of(2024, 10, 21);
        LocalDate endDate = LocalDate.of(2024, 10, 27);
        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atStartOfDay().plusDays(1);

        Record latestRecord = Record.builder()
                .id(2L)
                .deviceTag(deviceTag)
                .accuracy("encrypted-accuracy")
                .duration("encrypted-duration")
                .steps("encrypted-steps")
                .createdAt(startDateTime.minusDays(1))
                .build();

        List<Record> recordsBetweenDates = List.of(record);

        when(userService.getCurrentDeviceTag()).thenReturn(deviceTag);
        when(recordRepository.findLatestBeforeStartDate(deviceTag, startDateTime))
                .thenReturn(Optional.of(latestRecord));
        when(recordRepository.findByDateBetween(deviceTag, startDateTime, endDateTime))
                .thenReturn(recordsBetweenDates);
        when(encryptionUtils.decrypt(anyString())).thenReturn("95.5", "120", "1000");
        when(mapper.toDto(anyDouble(), anyInt(), anyInt(), any(LocalDate.class)))
                .thenReturn(
                        RecordResponseDto.builder()
                                .accuracy(95.5)
                                .duration(120)
                                .steps(1000)
                                .build(),
                        RecordResponseDto.builder()
                                .accuracy(95.5)
                                .duration(120)
                                .steps(1000)
                                .build()
                );

        // when
        List<RecordResponseDto> result = recordService.getRecordsByDateRange(startDate, endDate);

        // then
        assertThat(result).hasSize(2);
        verify(userService).getCurrentDeviceTag();
        verify(recordRepository).findLatestBeforeStartDate(deviceTag, startDateTime);
        verify(recordRepository).findByDateBetween(deviceTag, startDateTime, endDateTime);
        verify(encryptionUtils, times(6)).decrypt(anyString()); // accuracy, duration, steps * 2 records
        verify(mapper, times(2)).toDto(anyDouble(), anyInt(), anyInt(), any(LocalDate.class));
    }

    @Test
    void 통계정보를_조회한다() {
        // given
        when(userService.getCurrentDeviceTag()).thenReturn(deviceTag);
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
        RecordStatisticsResponseDto result = recordService.getRecordStatistics();

        // then
        assertThat(result.getTodayWalkTrainingCount()).isEqualTo(2);
        assertThat(result.getWeeklyWalkTrainingCount()).isEqualTo(5);
        assertThat(result.getConsecutiveWalkTrainingDays()).isEqualTo(3);
        verify(userService).getCurrentDeviceTag();
        verify(recordRepository, times(2))
                .countByDeviceTagAndCreatedAtBetween(anyString(), any(LocalDateTime.class), any(LocalDateTime.class));
        verify(recordRepository).findCreatedAtByDeviceTagOrderByCreatedAtDesc(deviceTag);
        verify(mapper).toDto(2, 5, 3);
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
