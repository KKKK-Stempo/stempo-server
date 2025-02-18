package com.stempo.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import com.stempo.dto.DecryptedRecord;
import com.stempo.dto.response.PersonalRhythmSettingsResponseDto;
import com.stempo.dto.response.RecordReportResponseDto;
import com.stempo.dto.response.RhythmDataResponseDto;
import com.stempo.dto.response.RhythmReportResponseDto;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class RecordReportServiceImplTest {

    @Mock
    private RecordService recordService;

    @InjectMocks
    private RecordReportServiceImpl recordReportService;

    @Test
    void 보행_훈련_기록을_날짜범위로_조회한다() {
        // given
        List<String> deviceTags = List.of("tagA", "tagB");
        LocalDate startDate = LocalDate.of(2025, 1, 1);
        LocalDate endDate = LocalDate.of(2025, 1, 31);

        DecryptedRecord record1 = DecryptedRecord.builder()
            .id(1L)
            .deviceTag("tagA")
            .accuracy(95.0)
            .duration(120)
            .steps(1000)
            .leftFootAverageSpeed(1.0)
            .rightFootAverageSpeed(1.0)
            .bit(4)
            .bpm(120)
            .createdAt(LocalDateTime.of(2025, 1, 5, 10, 0))
            .build();
        DecryptedRecord record2 = DecryptedRecord.builder()
            .id(2L)
            .deviceTag("tagA")
            .accuracy(96.0)
            .duration(130)
            .steps(1100)
            .leftFootAverageSpeed(1.1)
            .rightFootAverageSpeed(1.1)
            .bit(5)
            .bpm(125)
            .createdAt(LocalDateTime.of(2025, 1, 10, 10, 0))
            .build();
        DecryptedRecord record3 = DecryptedRecord.builder()
            .id(3L)
            .deviceTag("tagB")
            .accuracy(97.0)
            .duration(140)
            .steps(1200)
            .leftFootAverageSpeed(1.2)
            .rightFootAverageSpeed(1.2)
            .bit(6)
            .bpm(130)
            .createdAt(LocalDateTime.of(2025, 1, 15, 10, 0))
            .build();

        List<DecryptedRecord> serviceRecords = List.of(record1, record2, record3);
        when(recordService.getByDeviceTagsAndDateRange(deviceTags, startDate, endDate))
            .thenReturn(serviceRecords);

        // when
        List<RecordReportResponseDto> report = recordReportService.getRecordReport(deviceTags, startDate, endDate);

        // then
        assertThat(report).hasSize(2);
        // "tagA" 그룹: 2개의 레코드가 오름차순 정렬되어 있어야 함.
        RecordReportResponseDto reportA = report.stream()
            .filter(r -> r.getDeviceTag().equals("tagA"))
            .findFirst()
            .orElse(null);
        assertThat(reportA).isNotNull();
        assertThat(reportA.getRecords()).hasSize(2);
        assertThat(reportA.getRecords().get(0).getCreatedAt())
            .isBefore(reportA.getRecords().get(1).getCreatedAt());
        // "tagB" 그룹: 1개의 레코드.
        RecordReportResponseDto reportB = report.stream()
            .filter(r -> r.getDeviceTag().equals("tagB"))
            .findFirst()
            .orElse(null);
        assertThat(reportB).isNotNull();
        assertThat(reportB.getRecords()).hasSize(1);
    }

    @Test
    void 보행_훈련에_사용된_리듬_데이터를_날짜범위로_조회한다() {
        // given
        List<String> deviceTags = List.of("tagC");
        LocalDate startDate = LocalDate.of(2025, 2, 1);
        LocalDate endDate = LocalDate.of(2025, 2, 28);

        DecryptedRecord record = DecryptedRecord.builder()
            .id(4L)
            .deviceTag("tagC")
            .bit(7)
            .bpm(110)
            .createdAt(LocalDateTime.of(2025, 2, 10, 10, 0))
            .build();
        List<DecryptedRecord> serviceRecords = List.of(record);
        when(recordService.getByDeviceTagsAndDateRange(deviceTags, startDate, endDate))
            .thenReturn(serviceRecords);

        // when
        List<RhythmReportResponseDto> rhythmReport = recordReportService.getRhythmReport(deviceTags, startDate,
            endDate);

        // then
        assertThat(rhythmReport).hasSize(1);
        RhythmReportResponseDto report = rhythmReport.getFirst();
        assertThat(report.getDeviceTag()).isEqualTo("tagC");
        assertThat(report.getRecords()).hasSize(1);
        RhythmDataResponseDto rhythmData = report.getRecords().getFirst();
        assertThat(rhythmData.getBit()).isEqualTo(7);
        assertThat(rhythmData.getBpm()).isEqualTo(110);
    }

    @Test
    void 사용자_맞춤형_리듬_설정값을_조회한다() {
        // given
        List<String> deviceTags = List.of("tagD");
        // 여러 레코드가 섞여 있는 경우, 첫번째는 온보딩 추천, 마지막은 최종 설정값으로 사용됨.
        DecryptedRecord record1 = DecryptedRecord.builder()
            .id(5L)
            .deviceTag("tagD")
            .bit(3)
            .bpm(100)
            .createdAt(LocalDateTime.of(2025, 3, 1, 9, 0))
            .build();
        DecryptedRecord record2 = DecryptedRecord.builder()
            .id(6L)
            .deviceTag("tagD")
            .bit(4)
            .bpm(105)
            .createdAt(LocalDateTime.of(2025, 3, 5, 9, 0))
            .build();
        DecryptedRecord record3 = DecryptedRecord.builder()
            .id(7L)
            .deviceTag("tagD")
            .bit(5)
            .bpm(110)
            .createdAt(LocalDateTime.of(2025, 3, 10, 9, 0))
            .build();

        List<DecryptedRecord> serviceRecords = List.of(record2, record3, record1);
        when(recordService.getByDeviceTags(deviceTags)).thenReturn(serviceRecords);

        // when
        List<PersonalRhythmSettingsResponseDto> settings = recordReportService.getPersonalRhythmSettings(deviceTags);

        // then
        // "tagD" 그룹이 하나 생성되어야 한다.
        assertThat(settings).hasSize(1);
        PersonalRhythmSettingsResponseDto setting = settings.getFirst();
        // 정렬 후, 첫번째 레코드는 record1, 마지막은 record3
        assertThat(setting.getDeviceTag()).isEqualTo("tagD");
        assertThat(setting.getOnboardingBit()).isEqualTo(3);
        assertThat(setting.getOnboardingBpm()).isEqualTo(100);
        assertThat(setting.getLastRecordBit()).isEqualTo(5);
        assertThat(setting.getLastRecordBpm()).isEqualTo(110);
    }
}
