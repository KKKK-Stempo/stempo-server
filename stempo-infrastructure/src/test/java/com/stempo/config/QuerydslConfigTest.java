package com.stempo.config;

import static org.assertj.core.api.Assertions.assertThat;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.stempo.test.config.TestConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(classes = {QuerydslConfig.class, TestConfig.class})
@ActiveProfiles("test")
class QuerydslConfigTest {

    @Autowired
    private JPAQueryFactory jpaQueryFactory;

    @Test
    void QueryDSL빈이_정상적으로_등록된다() {
        assertThat(jpaQueryFactory).isNotNull();
    }
}
