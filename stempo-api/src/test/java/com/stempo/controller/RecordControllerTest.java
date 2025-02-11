package com.stempo.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.stempo.dto.request.RecordRequestDto;
import com.stempo.dto.response.RecordItemDto;
import com.stempo.dto.response.RecordResponseDto;
import com.stempo.dto.response.RecordStatisticsResponseDto;
import com.stempo.service.RecordService;
import com.stempo.test.TestApplication;
import java.time.LocalDate;
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

@WebMvcTest(controllers = RecordController.class)
@ContextConfiguration(classes = TestApplication.class)
@ActiveProfiles("test")
class RecordControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RecordService recordService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithMockUser(roles = "USER")
    void 정상적으로_보행_훈련_기록을_생성한다() throws Exception {
        // given
        RecordRequestDto requestDto = new RecordRequestDto();
        requestDto.setAccuracy(95.5);
        requestDto.setDuration(30);
        requestDto.setSteps(5000);
        requestDto.setLeftFootAvgSpeed(1.0);
        requestDto.setRightFootAvgSpeed(1.0);
        requestDto.setBit(4);
        requestDto.setBpm(120);

        String expectedDeviceTag = "device123";

        when(recordService.recordTrainingData(any(RecordRequestDto.class)))
            .thenReturn(expectedDeviceTag);

        // when
        mockMvc.perform(post("/api/v1/records")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto)))
            // then
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data").value(expectedDeviceTag));
    }

    @Test
    @WithMockUser(roles = "USER")
    void 유효하지_않은_입력값으로_보행_훈련_기록을_생성시_예외가_발생한다() throws Exception {
        // given
        RecordRequestDto requestDto = new RecordRequestDto();
        requestDto.setAccuracy(-10.0);
        requestDto.setDuration(-5);
        requestDto.setSteps(-1000);

        // when
        mockMvc.perform(post("/api/v1/records")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto)))
            // then
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.success").value(false))
            .andExpect(jsonPath("$.data").isEmpty());
    }

    @Test
    void 인증되지_않은_사용자가_보행_훈련_기록을_생성시_권한에러가_발생한다() throws Exception {
        // given
        RecordRequestDto requestDto = new RecordRequestDto();
        requestDto.setAccuracy(95.5);
        requestDto.setDuration(30);
        requestDto.setSteps(5000);

        // when
        mockMvc.perform(post("/api/v1/records")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto)))
            // then
            .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "USER")
    void 정상적으로_보행_훈련_기록을_조회한다() throws Exception {
        // given
        LocalDate startDate = LocalDate.of(2024, 10, 1);
        LocalDate endDate = LocalDate.of(2024, 10, 31);

        List<RecordItemDto> recordItems = List.of(
            RecordItemDto.builder()
                .accuracy(95.5)
                .duration(30)
                .steps(5000)
                .date(LocalDate.of(2024, 10, 15))
                .build(),
            RecordItemDto.builder()
                .accuracy(90.0)
                .duration(25)
                .steps(4500)
                .date(LocalDate.of(2024, 10, 20))
                .build()
        );

        int accuracyAverage = 93;

        RecordResponseDto responseDto = RecordResponseDto.builder()
            .accuracyAverage(accuracyAverage)
            .records(recordItems)
            .build();

        when(recordService.getRecordsByDateRange(startDate, endDate))
            .thenReturn(responseDto);

        // when
        mockMvc.perform(get("/api/v1/records")
                .param("startDate", "2024-10-01")
                .param("endDate", "2024-10-31")
                .contentType(MediaType.APPLICATION_JSON))
            // then
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data.accuracyAverage").value(93))
            .andExpect(jsonPath("$.data.records[0].accuracy").value(95.5))
            .andExpect(jsonPath("$.data.records[0].duration").value(30))
            .andExpect(jsonPath("$.data.records[0].steps").value(5000))
            .andExpect(jsonPath("$.data.records[0].date").value("2024-10-15"))
            .andExpect(jsonPath("$.data.records[1].accuracy").value(90.0))
            .andExpect(jsonPath("$.data.records[1].duration").value(25))
            .andExpect(jsonPath("$.data.records[1].steps").value(4500))
            .andExpect(jsonPath("$.data.records[1].date").value("2024-10-20"));
    }

    @Test
    void 인증되지_않은_사용자가_보행_훈련_기록을_조회시_권한에러가_발생한다() throws Exception {
        // when
        mockMvc.perform(get("/api/v1/records")
                .param("startDate", "2024-10-01")
                .param("endDate", "2024-10-31")
                .contentType(MediaType.APPLICATION_JSON))
            // then
            .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "USER")
    void 정상적으로_보행_훈련_기록_통계를_조회한다() throws Exception {
        // given
        RecordStatisticsResponseDto expectedStatistics = RecordStatisticsResponseDto.builder()
            .todayWalkTrainingCount(10)
            .weeklyWalkTrainingCount(50)
            .consecutiveWalkTrainingDays(5)
            .build();

        when(recordService.getRecordStatistics())
            .thenReturn(expectedStatistics);

        // when
        mockMvc.perform(get("/api/v1/records/statistics")
                .contentType(MediaType.APPLICATION_JSON))
            // then
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data.todayWalkTrainingCount").value(10))
            .andExpect(jsonPath("$.data.weeklyWalkTrainingCount").value(50))
            .andExpect(jsonPath("$.data.consecutiveWalkTrainingDays").value(5));
    }

    @Test
    void 인증되지_않은_사용자가_보행_훈련_기록_통계를_조회시_권한에러가_발생한다() throws Exception {
        // when
        mockMvc.perform(get("/api/v1/records/statistics")
                .contentType(MediaType.APPLICATION_JSON))
            // then
            .andExpect(status().isUnauthorized());
    }
}
