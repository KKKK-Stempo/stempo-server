package com.stempo.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.stempo.dto.PagedResponseDto;
import com.stempo.dto.request.HomeworkRequestDto;
import com.stempo.dto.request.HomeworkUpdateRequestDto;
import com.stempo.dto.response.HomeworkResponseDto;
import com.stempo.service.HomeworkService;
import com.stempo.test.TestApplication;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = HomeworkController.class)
@ContextConfiguration(classes = TestApplication.class)
@ActiveProfiles("test")
class HomeworkControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private HomeworkService homeworkService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithMockUser(username = "device123", roles = "USER")
    void 정상적으로_과제를_추가한다() throws Exception {
        // given
        String deviceTag = "device123";
        HomeworkRequestDto requestDto = new HomeworkRequestDto();
        requestDto.setDescription("매일 스트레칭 운동 진행");

        Long expectedHomeworkId = 1L;

        when(homeworkService.addHomework(eq(deviceTag), any(HomeworkRequestDto.class)))
            .thenReturn(expectedHomeworkId);

        // when
        mockMvc.perform(post("/api/v1/homeworks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto)))
            // then
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data").value(expectedHomeworkId));
    }

    @Test
    @WithMockUser(roles = "USER")
    void 유효하지_않은_입력값으로_과제를_추가시_예외가_발생한다() throws Exception {
        // given
        HomeworkRequestDto requestDto = new HomeworkRequestDto();
        requestDto.setDescription("");

        // when
        mockMvc.perform(post("/api/v1/homeworks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto)))
            // then
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.success").value(false))
            .andExpect(jsonPath("$.data").isEmpty());
    }

    @Test
    void 인증되지_않은_사용자가_과제를_추가시_권한에러가_발생한다() throws Exception {
        // given
        HomeworkRequestDto requestDto = new HomeworkRequestDto();
        requestDto.setDescription("매일 스트레칭 운동 진행");

        // when
        mockMvc.perform(post("/api/v1/homeworks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto)))
            // then
            .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "USER")
    void 정상적으로_과제를_조회한다() throws Exception {
        // given
        Pageable pageable = PageRequest.of(0, 10, Sort.by("completed").ascending().and(Sort.by("id").ascending()));

        PagedResponseDto<HomeworkResponseDto> expectedPagedResponse = new PagedResponseDto<>(
            List.of(
                HomeworkResponseDto.builder()
                    .id(1L)
                    .description("매일 스트레칭 운동 진행")
                    .completed(false)
                    .build(),
                HomeworkResponseDto.builder()
                    .id(2L)
                    .description("주 3회 보행 훈련 참석")
                    .completed(true)
                    .build()
            ),
            pageable,
            2
        );

        when(homeworkService.getHomeworks(
            any(String.class),
            eq(null),
            any(Pageable.class)))
            .thenReturn(expectedPagedResponse);

        // when
        mockMvc.perform(get("/api/v1/homeworks")
                .param("page", "0")
                .param("size", "10")
                .contentType(MediaType.APPLICATION_JSON))
            // then
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data.items").isArray())
            .andExpect(jsonPath("$.data.items[0].id").value(1))
            .andExpect(jsonPath("$.data.items[0].description").value("매일 스트레칭 운동 진행"))
            .andExpect(jsonPath("$.data.items[0].completed").value(false))
            .andExpect(jsonPath("$.data.items[1].id").value(2))
            .andExpect(jsonPath("$.data.items[1].description").value("주 3회 보행 훈련 참석"))
            .andExpect(jsonPath("$.data.items[1].completed").value(true));
    }

    @Test
    void 인증되지_않은_사용자가_과제를_조회시_권한에러가_발생한다() throws Exception {
        // when
        mockMvc.perform(get("/api/v1/homeworks")
                .param("page", "0")
                .param("size", "10")
                .contentType(MediaType.APPLICATION_JSON))
            // then
            .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "USER")
    void 정상적으로_과제를_수정한다() throws Exception {
        // given
        Long homeworkId = 1L;
        HomeworkUpdateRequestDto updateRequestDto = new HomeworkUpdateRequestDto();
        updateRequestDto.setDescription("매일 아침 보행 훈련 진행");

        Long expectedHomeworkId = 1L;

        when(homeworkService.updateHomework(eq(homeworkId), any(HomeworkUpdateRequestDto.class)))
            .thenReturn(expectedHomeworkId);

        // when
        mockMvc.perform(patch("/api/v1/homeworks/{homeworkId}", homeworkId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequestDto)))
            // then
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data").value(expectedHomeworkId));
    }

    @Test
    void 인증되지_않은_사용자가_과제를_수정시_권한에러가_발생한다() throws Exception {
        // given
        Long homeworkId = 1L;
        HomeworkUpdateRequestDto updateRequestDto = new HomeworkUpdateRequestDto();
        updateRequestDto.setDescription("매일 아침 보행 훈련 진행");

        // when
        mockMvc.perform(patch("/api/v1/homeworks/{homeworkId}", homeworkId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequestDto)))
            // then
            .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "USER")
    void 정상적으로_과제를_삭제한다() throws Exception {
        // given
        Long homeworkId = 1L;

        when(homeworkService.deleteHomework(homeworkId))
            .thenReturn(homeworkId);

        // when
        mockMvc.perform(delete("/api/v1/homeworks/{homeworkId}", homeworkId)
                .contentType(MediaType.APPLICATION_JSON))
            // then
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data").value(homeworkId));
    }

    @Test
    void 인증되지_않은_사용자가_과제를_삭제시_권한에러가_발생한다() throws Exception {
        // given
        Long homeworkId = 1L;

        // when
        mockMvc.perform(delete("/api/v1/homeworks/{homeworkId}", homeworkId)
                .contentType(MediaType.APPLICATION_JSON))
            // then
            .andExpect(status().isUnauthorized());
    }
}
