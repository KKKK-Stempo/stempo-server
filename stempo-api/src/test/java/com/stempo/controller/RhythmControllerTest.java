package com.stempo.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.stempo.dto.request.RhythmRequestDto;
import com.stempo.service.RhythmService;
import com.stempo.test.TestApplication;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = RhythmController.class)
@ContextConfiguration(classes = TestApplication.class)
@ActiveProfiles("test")
public class RhythmControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RhythmService rhythmService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithMockUser(roles = "USER")
    void 정상적으로_리듬을_생성한다() throws Exception {
        // given
        RhythmRequestDto requestDto = new RhythmRequestDto();
        requestDto.setBpm(120);
        requestDto.setBit(4);

        String expectedFilePath = "/resources/files/rhythm_120_4_bpm.wav";

        when(rhythmService.createRhythm(any(RhythmRequestDto.class))).thenReturn(expectedFilePath);

        // when
        mockMvc.perform(post("/api/v1/rhythm").contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                // then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").value(expectedFilePath));
    }

    @Test
    @WithMockUser(roles = "USER")
    void 유효하지_않은_입력값으로_리듬_생성시_예외가_발생한다() throws Exception {
        // given
        RhythmRequestDto requestDto = new RhythmRequestDto();
        requestDto.setBpm(5);
        requestDto.setBit(4);

        // when
        mockMvc.perform(post("/api/v1/rhythm").contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                // then
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.data").isEmpty());
    }

    @Test
    void 인증되지_않은_사용자가_리듬_생성시_권한에러가_발생한다() throws Exception {
        // given
        RhythmRequestDto requestDto = new RhythmRequestDto();
        requestDto.setBpm(120);
        requestDto.setBit(4);

        // when
        mockMvc.perform(post("/api/v1/rhythm").contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                // then
                .andExpect(status().isUnauthorized());
    }
}
