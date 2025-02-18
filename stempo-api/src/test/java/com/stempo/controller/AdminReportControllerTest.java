package com.stempo.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.stempo.dto.response.HomeworkDataResponseDto;
import com.stempo.dto.response.PersonalRhythmSettingsResponseDto;
import com.stempo.dto.response.PersonalTrainingSettingsResponseDto;
import com.stempo.dto.response.RecordDataResponseDto;
import com.stempo.dto.response.RecordReportResponseDto;
import com.stempo.dto.response.RhythmDataResponseDto;
import com.stempo.dto.response.RhythmReportResponseDto;
import com.stempo.dto.response.UserDataResponseDto;
import com.stempo.service.RecordReportService;
import com.stempo.service.UserDataAggregationService;
import com.stempo.test.TestApplication;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = AdminReportController.class)
@ContextConfiguration(classes = {TestApplication.class})
@ActiveProfiles("test")
class AdminReportControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserDataAggregationService userDataAggregationService;

    @MockBean
    private RecordReportService recordReportService;

    @Test
    @WithMockUser(roles = "ADMIN")
    void 정상적으로_전체_이용_데이터를_조회한다() throws Exception {
        // given
        RecordDataResponseDto recordDto = RecordDataResponseDto.builder()
            .accuracy(0.0)
            .duration(0)
            .steps(0)
            .leftFootAverageSpeed(0.0)
            .rightFootAverageSpeed(0.0)
            .bit(4)
            .bpm(60)
            .createdAt(LocalDateTime.of(2025, 1, 1, 0, 0))
            .build();
        HomeworkDataResponseDto homeworkDto = HomeworkDataResponseDto.builder()
            .description("매일 스트레칭 운동 진행")
            .completed(false)
            .createdAt(LocalDateTime.of(2025, 1, 1, 0, 0))
            .updatedAt(LocalDateTime.of(2025, 1, 1, 0, 0))
            .build();
        UserDataResponseDto userData = UserDataResponseDto.builder()
            .deviceTag("490154203237518")
            .records(List.of(recordDto))
            .homeworks(List.of(homeworkDto))
            .build();

        when(userDataAggregationService.getUserData(any())).thenReturn(List.of(userData));

        // when & then
        mockMvc.perform(get("/api/v1/admin/report/user-data")
                .param("deviceTags", "490154203237518")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            // 응답의 deviceTag 검증
            .andExpect(jsonPath("$.data[0].deviceTag").value("490154203237518"))
            // records 배열 검증
            .andExpect(jsonPath("$.data[0].records[0].accuracy").value(0.0))
            .andExpect(jsonPath("$.data[0].records[0].duration").value(0))
            .andExpect(jsonPath("$.data[0].records[0].steps").value(0))
            .andExpect(jsonPath("$.data[0].records[0].leftFootAverageSpeed").value(0.0))
            .andExpect(jsonPath("$.data[0].records[0].rightFootAverageSpeed").value(0.0))
            .andExpect(jsonPath("$.data[0].records[0].bit").value(4))
            .andExpect(jsonPath("$.data[0].records[0].bpm").value(60))
            .andExpect(jsonPath("$.data[0].records[0].createdAt").value("2025-01-01T00:00:00"))
            // homeworks 배열 검증
            .andExpect(jsonPath("$.data[0].homeworks[0].description").value("매일 스트레칭 운동 진행"))
            .andExpect(jsonPath("$.data[0].homeworks[0].completed").value(false))
            .andExpect(jsonPath("$.data[0].homeworks[0].createdAt").value("2025-01-01T00:00:00"))
            .andExpect(jsonPath("$.data[0].homeworks[0].updatedAt").value("2025-01-01T00:00:00"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void 정상적으로_보행_훈련_기록을_조회한다() throws Exception {
        // given
        LocalDate startDate = LocalDate.of(2025, 1, 1);
        LocalDate endDate = LocalDate.of(2025, 1, 31);
        RecordDataResponseDto recordDto = RecordDataResponseDto.builder()
            .accuracy(1.0)
            .duration(30)
            .steps(100)
            .leftFootAverageSpeed(0.8)
            .rightFootAverageSpeed(0.9)
            .bit(4)
            .bpm(60)
            .createdAt(LocalDateTime.of(2025, 1, 15, 10, 0))
            .build();
        RecordReportResponseDto recordReport = RecordReportResponseDto.builder()
            .deviceTag("490154203237518")
            .records(List.of(recordDto))
            .build();
        when(recordReportService.getRecordReport(any(), eq(startDate), eq(endDate)))
            .thenReturn(List.of(recordReport));

        // when & then
        mockMvc.perform(get("/api/v1/admin/report/user-data/training")
                .param("deviceTags", "490154203237518")
                .param("startDate", startDate.toString())
                .param("endDate", endDate.toString())
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            // deviceTag 검증
            .andExpect(jsonPath("$.data[0].deviceTag").value("490154203237518"))
            // records 배열 검증
            .andExpect(jsonPath("$.data[0].records[0].accuracy").value(1.0))
            .andExpect(jsonPath("$.data[0].records[0].duration").value(30))
            .andExpect(jsonPath("$.data[0].records[0].steps").value(100))
            .andExpect(jsonPath("$.data[0].records[0].leftFootAverageSpeed").value(0.8))
            .andExpect(jsonPath("$.data[0].records[0].rightFootAverageSpeed").value(0.9))
            .andExpect(jsonPath("$.data[0].records[0].bit").value(4))
            .andExpect(jsonPath("$.data[0].records[0].bpm").value(60))
            .andExpect(jsonPath("$.data[0].records[0].createdAt").value("2025-01-15T10:00:00"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void 정상적으로_보행_분석_지표_설정값을_조회한다() throws Exception {
        // given
        RecordDataResponseDto initialTraining = RecordDataResponseDto.builder()
            .accuracy(0.5)
            .duration(20)
            .steps(50)
            .leftFootAverageSpeed(0.7)
            .rightFootAverageSpeed(0.8)
            .bit(4)
            .bpm(60)
            .createdAt(LocalDateTime.of(2025, 1, 5, 9, 0))
            .build();
        HomeworkDataResponseDto homeworkDto = HomeworkDataResponseDto.builder()
            .description("매일 스트레칭 운동 진행")
            .completed(false)
            .createdAt(LocalDateTime.of(2025, 1, 5, 9, 5))
            .updatedAt(LocalDateTime.of(2025, 1, 5, 9, 10))
            .build();
        PersonalTrainingSettingsResponseDto trainingSettings = PersonalTrainingSettingsResponseDto.builder()
            .deviceTag("490154203237518")
            .initialTraining(initialTraining)
            .homeworks(List.of(homeworkDto))
            .build();
        when(userDataAggregationService.getPersonalTrainingSettings(any()))
            .thenReturn(List.of(trainingSettings));

        // when & then
        mockMvc.perform(get("/api/v1/admin/report/user-data/training-setting")
                .param("deviceTags", "490154203237518")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            // PersonalTrainingSettingsResponseDto 검증
            .andExpect(jsonPath("$.data[0].deviceTag").value("490154203237518"))
            .andExpect(jsonPath("$.data[0].initialTraining.accuracy").value(0.5))
            .andExpect(jsonPath("$.data[0].initialTraining.duration").value(20))
            .andExpect(jsonPath("$.data[0].initialTraining.steps").value(50))
            .andExpect(jsonPath("$.data[0].initialTraining.leftFootAverageSpeed").value(0.7))
            .andExpect(jsonPath("$.data[0].initialTraining.rightFootAverageSpeed").value(0.8))
            .andExpect(jsonPath("$.data[0].initialTraining.bit").value(4))
            .andExpect(jsonPath("$.data[0].initialTraining.bpm").value(60))
            .andExpect(jsonPath("$.data[0].initialTraining.createdAt").value("2025-01-05T09:00:00"))
            .andExpect(jsonPath("$.data[0].homeworks[0].description").value("매일 스트레칭 운동 진행"))
            .andExpect(jsonPath("$.data[0].homeworks[0].completed").value(false))
            .andExpect(jsonPath("$.data[0].homeworks[0].createdAt").value("2025-01-05T09:05:00"))
            .andExpect(jsonPath("$.data[0].homeworks[0].updatedAt").value("2025-01-05T09:10:00"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void 정상적으로_리듬_데이터를_조회한다() throws Exception {
        // given
        LocalDate startDate = LocalDate.of(2025, 1, 1);
        LocalDate endDate = LocalDate.of(2025, 1, 31);
        RhythmDataResponseDto rhythmData = RhythmDataResponseDto.builder()
            .bit(4)
            .bpm(60)
            .createdAt(LocalDateTime.of(2025, 1, 10, 11, 0))
            .build();
        RhythmReportResponseDto rhythmReport = RhythmReportResponseDto.builder()
            .deviceTag("490154203237518")
            .records(List.of(rhythmData))
            .build();
        when(recordReportService.getRhythmReport(any(), eq(startDate), eq(endDate)))
            .thenReturn(List.of(rhythmReport));

        // when & then
        mockMvc.perform(get("/api/v1/admin/report/user-data/rhythm")
                .param("deviceTags", "490154203237518")
                .param("startDate", startDate.toString())
                .param("endDate", endDate.toString())
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            // RhythmReportResponseDto 검증
            .andExpect(jsonPath("$.data[0].deviceTag").value("490154203237518"))
            .andExpect(jsonPath("$.data[0].records[0].bit").value(4))
            .andExpect(jsonPath("$.data[0].records[0].bpm").value(60))
            .andExpect(jsonPath("$.data[0].records[0].createdAt").value("2025-01-10T11:00:00"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void 정상적으로_맞춤형_리듬_설정값을_조회한다() throws Exception {
        // given
        PersonalRhythmSettingsResponseDto rhythmSettings = PersonalRhythmSettingsResponseDto.builder()
            .deviceTag("490154203237518")
            .onboardingBit(4)
            .onboardingBpm(60)
            .lastRecordBit(6)
            .lastRecordBpm(80)
            .build();
        when(recordReportService.getPersonalRhythmSettings(any()))
            .thenReturn(List.of(rhythmSettings));

        // when & then
        mockMvc.perform(get("/api/v1/admin/report/user-data/rhythm-setting")
                .param("deviceTags", "490154203237518")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            // PersonalRhythmSettingsResponseDto 검증
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data[0].deviceTag").value("490154203237518"))
            .andExpect(jsonPath("$.data[0].onboardingBit").value(4))
            .andExpect(jsonPath("$.data[0].onboardingBpm").value(60))
            .andExpect(jsonPath("$.data[0].lastRecordBit").value(6))
            .andExpect(jsonPath("$.data[0].lastRecordBpm").value(80));
    }
}
