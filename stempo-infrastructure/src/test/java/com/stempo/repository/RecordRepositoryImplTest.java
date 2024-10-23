package com.stempo.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.stempo.entity.RecordEntity;
import com.stempo.mapper.RecordMapper;
import com.stempo.model.Record;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class RecordRepositoryImplTest {

    @Mock
    private RecordJpaRepository recordJpaRepository;

    @Mock
    private RecordMapper recordMapper;

    @InjectMocks
    private RecordRepositoryImpl recordRepository;

    private Record record;
    private RecordEntity recordEntity;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        record = Record.builder()
                .id(1L)
                .deviceTag("device123")
                .accuracy("90")
                .duration("10")
                .steps("1000")
                .createdAt(LocalDateTime.now())
                .build();

        recordEntity = RecordEntity.builder()
                .id(1L)
                .deviceTag("device123")
                .accuracy("90")
                .duration("10")
                .steps("1000")
                .build();
        recordEntity.setCreatedAt(LocalDateTime.now());
    }

    @Test
    void 기록을_저장한다() {
        // given
        when(recordMapper.toEntity(any(Record.class))).thenReturn(recordEntity);
        when(recordJpaRepository.save(any(RecordEntity.class))).thenReturn(recordEntity);
        when(recordMapper.toDomain(any(RecordEntity.class))).thenReturn(record);

        // when
        Record savedRecord = recordRepository.save(record);

        // then
        assertThat(savedRecord).isNotNull();
        assertThat(savedRecord.getId()).isEqualTo(record.getId());
        verify(recordJpaRepository, times(1)).save(recordEntity);
    }

    @Test
    void 기록을_삭제한다() {
        // given
        when(recordMapper.toEntity(any(Record.class))).thenReturn(recordEntity);

        // when
        recordRepository.deleteAll(List.of(record));

        // then
        verify(recordJpaRepository, times(1)).deleteAll(List.of(recordEntity));
    }

    @Test
    void 기록을_모두_삭제한다() {
        // given
        when(recordMapper.toEntity(any(Record.class))).thenReturn(recordEntity);

        // when
        recordRepository.deleteAll(List.of(record));

        // then
        verify(recordJpaRepository, times(1)).deleteAll(List.of(recordEntity));
    }

    @Test
    void 날짜_범위로_기록을_조회한다() {
        // given
        LocalDateTime startDateTime = LocalDateTime.now().minusDays(1);
        LocalDateTime endDateTime = LocalDateTime.now();
        when(recordJpaRepository.findByDateBetween(anyString(), any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(List.of(recordEntity));
        when(recordMapper.toDomain(any(RecordEntity.class))).thenReturn(record);

        // when
        List<Record> records = recordRepository.findByDateBetween("device123", startDateTime, endDateTime);

        // then
        assertThat(records).hasSize(1);
        assertThat(records.getFirst().getDeviceTag()).isEqualTo("device123");
    }

    @Test
    void 시작_날짜_이전의_최근_기록을_조회한다() {
        // given
        LocalDateTime startDateTime = LocalDateTime.now().minusDays(1);
        when(recordJpaRepository.findLatestBeforeStartDate(anyString(), any(LocalDateTime.class)))
                .thenReturn(Optional.of(recordEntity));
        when(recordMapper.toDomain(any(RecordEntity.class))).thenReturn(record);

        // when
        Optional<Record> foundRecord = recordRepository.findLatestBeforeStartDate("device123", startDateTime);

        // then
        assertThat(foundRecord).isPresent();
        assertThat(foundRecord.get().getDeviceTag()).isEqualTo("device123");
    }

    @Test
    void 디바이스_태그로_기록을_조회한다() {
        // given
        when(recordJpaRepository.findByDeviceTag(anyString())).thenReturn(List.of(recordEntity));
        when(recordMapper.toDomain(any(RecordEntity.class))).thenReturn(record);

        // when
        List<Record> records = recordRepository.findByDeviceTag("device123");

        // then
        assertThat(records).hasSize(1);
        assertThat(records.getFirst().getDeviceTag()).isEqualTo("device123");
    }

    @Test
    void 디바이스_태그로_생성날짜를_조회한다() {
        // given
        LocalDateTime now = LocalDateTime.now();
        when(recordJpaRepository.findCreatedAtByDeviceTagOrderByCreatedAtDesc(anyString()))
                .thenReturn(List.of(now));

        // when
        List<LocalDateTime> dateTimes = recordRepository.findCreatedAtByDeviceTagOrderByCreatedAtDesc("device123");

        // then
        assertThat(dateTimes).hasSize(1);
        assertThat(dateTimes.getFirst()).isEqualTo(now);
    }

    @Test
    void 날짜_범위로_기록_수를_카운트한다() {
        // given
        LocalDateTime startDateTime = LocalDateTime.now().minusDays(1);
        LocalDateTime endDateTime = LocalDateTime.now();
        when(recordJpaRepository.countByDeviceTagAndCreatedAtBetween(anyString(), any(LocalDateTime.class),
                any(LocalDateTime.class)))
                .thenReturn(5);

        // when
        int count = recordRepository.countByDeviceTagAndCreatedAtBetween("device123", startDateTime, endDateTime);

        // then
        assertThat(count).isEqualTo(5);
    }
}
