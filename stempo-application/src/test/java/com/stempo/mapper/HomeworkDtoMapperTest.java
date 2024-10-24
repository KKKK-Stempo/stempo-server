package com.stempo.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.stempo.dto.request.HomeworkUpdateRequestDto;
import com.stempo.dto.response.HomeworkResponseDto;
import com.stempo.model.Homework;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class HomeworkDtoMapperTest {

    private HomeworkDtoMapper homeworkDtoMapper;

    @BeforeEach
    void setUp() {
        homeworkDtoMapper = new HomeworkDtoMapper();
    }

    @Test
    void HomeworkUpdateRequestDto에서_Homework로_매핑된다() {
        // given
        HomeworkUpdateRequestDto requestDto = new HomeworkUpdateRequestDto();
        requestDto.setDescription("Test Homework");
        requestDto.setCompleted(true);

        // when
        Homework homework = homeworkDtoMapper.toDomain(requestDto);

        // then
        assertThat(homework.getDescription()).isEqualTo("Test Homework");
        assertThat(homework.getCompleted()).isTrue();
    }

    @Test
    void Homework에서_HomeworkResponseDto로_매핑된다() {
        // given
        Homework homework = Homework.builder()
                .id(1L)
                .description("Test Homework")
                .completed(true)
                .build();

        // when
        HomeworkResponseDto responseDto = homeworkDtoMapper.toDto(homework);

        // then
        assertThat(responseDto.getId()).isEqualTo(1L);
        assertThat(responseDto.getDescription()).isEqualTo("Test Homework");
        assertThat(responseDto.isCompleted()).isTrue();
    }
}
