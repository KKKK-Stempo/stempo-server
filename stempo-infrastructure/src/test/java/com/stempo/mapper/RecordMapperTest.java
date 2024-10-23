package com.stempo.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.stempo.entity.RecordEntity;
import com.stempo.model.Record;
import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class RecordMapperTest {

    private RecordMapper recordMapper;

    @BeforeEach
    void setUp() {
        recordMapper = new RecordMapper();
    }

    @Test
    void 도메인을_엔티티로_매핑한다() {
        // given
        Record record = Record.builder()
                .id(1L)
                .deviceTag("device123")
                .accuracy("95.5")
                .duration("120")
                .steps("1000")
                .createdAt(LocalDateTime.now())
                .build();

        // when
        RecordEntity entity = recordMapper.toEntity(record);

        // then
        assertThat(entity.getId()).isEqualTo(record.getId());
        assertThat(entity.getDeviceTag()).isEqualTo(record.getDeviceTag());
        assertThat(entity.getAccuracy()).isEqualTo("95.5");
        assertThat(entity.getDuration()).isEqualTo("120");
        assertThat(entity.getSteps()).isEqualTo("1000");
    }

    @Test
    void 엔티티를_도메인으로_매핑한다() {
        // given
        RecordEntity entity = RecordEntity.builder()
                .id(1L)
                .deviceTag("device123")
                .accuracy("95.5")
                .duration("120")
                .steps("1000")
                .build();
        entity.setCreatedAt(LocalDateTime.now());

        // when
        Record record = recordMapper.toDomain(entity);

        // then
        assertThat(record.getId()).isEqualTo(entity.getId());
        assertThat(record.getDeviceTag()).isEqualTo(entity.getDeviceTag());
        assertThat(record.getAccuracy()).isEqualTo("95.5");
        assertThat(record.getDuration()).isEqualTo("120");
        assertThat(record.getSteps()).isEqualTo("1000");
        assertThat(record.getCreatedAt()).isEqualTo(entity.getCreatedAt());
    }
}
