package com.stempo.entity;

import static org.assertj.core.api.Assertions.assertThat;

import com.stempo.config.JpaConfig;
import com.stempo.test.config.TestConfig;
import com.stempo.test.entity.TestEntity;
import com.stempo.test.repository.TestJpaRepository;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

@DataJpaTest
@ContextConfiguration(classes = {TestConfig.class})
@Import(JpaConfig.class)
@ActiveProfiles("test")
class BaseEntityTest {

    @Autowired
    private TestJpaRepository repository;

    private TestEntity testEntity;

    @BeforeEach
    void setUp() {
        testEntity = new TestEntity();
        testEntity.setName("TEST ENTITY");
    }

    @Test
    void 엔티티를_저장하면_생성시간과_수정시간이_기록된다() {
        // given
        LocalDateTime beforeSave = LocalDateTime.now();

        // when
        TestEntity savedEntity = repository.save(testEntity);

        // then
        assertThat(savedEntity.getCreatedAt()).isNotNull();
        assertThat(savedEntity.getUpdatedAt()).isNotNull();
        assertThat(savedEntity.getCreatedAt()).isAfterOrEqualTo(beforeSave);
        assertThat(savedEntity.getUpdatedAt()).isAfterOrEqualTo(beforeSave);
    }

    @Test
    void 엔티티를_수정하면_수정시간이_갱신된다() {
        // given
        TestEntity savedEntity = repository.save(testEntity);
        LocalDateTime initialUpdatedAt = savedEntity.getUpdatedAt();

        // when
        savedEntity.setName("UPDATED TEST ENTITY");
        TestEntity updatedEntity = repository.save(savedEntity);

        // then
        assertThat(updatedEntity.getUpdatedAt()).isNotNull();
        assertThat(updatedEntity.getUpdatedAt()).isAfter(initialUpdatedAt.truncatedTo(ChronoUnit.SECONDS));
    }
}
