package com.stempo.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.stempo.entity.HomeworkEntity;
import com.stempo.model.Homework;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class HomeworkMapperTest {

    private HomeworkMapper homeworkMapper;

    @BeforeEach
    void setUp() {
        homeworkMapper = new HomeworkMapper();
    }

    @Test
    void 도메인을_엔티티로_매핑한다() {
        // given
        Homework homework = Homework.builder()
                .id(1L)
                .deviceTag("device123")
                .description("Test Description")
                .completed(true)
                .build();

        // when
        HomeworkEntity entity = homeworkMapper.toEntity(homework);

        // then
        assertThat(entity.getId()).isEqualTo(homework.getId());
        assertThat(entity.getDeviceTag()).isEqualTo(homework.getDeviceTag());
        assertThat(entity.getDescription()).isEqualTo(homework.getDescription());
        assertThat(entity.isCompleted()).isEqualTo(homework.getCompleted());
    }

    @Test
    void 엔티티를_도메인으로_매핑한다() {
        // given
        HomeworkEntity entity = HomeworkEntity.builder()
                .id(1L)
                .deviceTag("device123")
                .description("Test Description")
                .completed(true)
                .build();

        // when
        Homework homework = homeworkMapper.toDomain(entity);

        // then
        assertThat(homework.getId()).isEqualTo(entity.getId());
        assertThat(homework.getDeviceTag()).isEqualTo(entity.getDeviceTag());
        assertThat(homework.getDescription()).isEqualTo(entity.getDescription());
        assertThat(homework.getCompleted()).isEqualTo(entity.isCompleted());
    }

    @Test
    void 도메인을_엔티티로_매핑할_때_completed가_null인_경우_false로_변환된다() {
        // given
        Homework homework = Homework.builder()
                .id(1L)
                .deviceTag("device123")
                .description("Test Description")
                .completed(null)
                .build();

        // when
        HomeworkEntity entity = homeworkMapper.toEntity(homework);

        // then
        assertThat(entity.isCompleted()).isFalse();
    }

    @Test
    void 엔티티_리스트를_도메인_리스트로_매핑한다() {
        // given
        HomeworkEntity entity1 = HomeworkEntity.builder()
                .id(1L)
                .deviceTag("device123")
                .description("Test Description 1")
                .completed(true)
                .build();

        HomeworkEntity entity2 = HomeworkEntity.builder()
                .id(2L)
                .deviceTag("device456")
                .description("Test Description 2")
                .completed(false)
                .build();

        List<HomeworkEntity> entities = List.of(entity1, entity2);

        // when
        List<Homework> homeworks = homeworkMapper.toDomain(entities);

        // then
        assertThat(homeworks).hasSize(2);
        assertThat(homeworks.get(0).getId()).isEqualTo(entity1.getId());
        assertThat(homeworks.get(1).getId()).isEqualTo(entity2.getId());
        assertThat(homeworks.get(0).getDeviceTag()).isEqualTo(entity1.getDeviceTag());
        assertThat(homeworks.get(1).getDeviceTag()).isEqualTo(entity2.getDeviceTag());
        assertThat(homeworks.get(0).getDescription()).isEqualTo(entity1.getDescription());
        assertThat(homeworks.get(1).getDescription()).isEqualTo(entity2.getDescription());
        assertThat(homeworks.get(0).getCompleted()).isTrue();
        assertThat(homeworks.get(1).getCompleted()).isFalse();
    }
}
